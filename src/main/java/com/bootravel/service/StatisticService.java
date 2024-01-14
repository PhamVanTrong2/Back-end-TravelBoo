package com.bootravel.service;

import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.payload.responses.*;
import com.bootravel.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class StatisticService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TransactionsRepository transactionRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    public DataNumberSystemResponse dataSystem() throws Exception {
        DataNumberSystemResponse response = new DataNumberSystemResponse();

        int totalHotel = hotelRepository.totalHotelSystem();
        int totalUser = userRepository.totalUserSystem();
        int totalBooking = bookingRepository.totalBookingSystem();
        BigDecimal totalRevenue = transactionRepository.totalRevenueSystem();
        if(totalRevenue != null) {
            BigDecimal totalProfit = totalRevenue.multiply(new BigDecimal(15)).divide(new BigDecimal(100));
            response.setTotalProfit(totalProfit);
        }
        int totalPromotion = promotionRepository.totalPromotionSystem();

        response.setTotalHotel(totalHotel);
        response.setTotalUser(totalUser);
        response.setTotalBooking(totalBooking);
        response.setTotalRevenue(totalRevenue);
        response.setTotalPromotion(totalPromotion);
        
        return response;
    }

    public TotalIncomeResponse totalIncomeSystem() {
        TotalIncomeResponse response = new TotalIncomeResponse();
        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        Month sixMonthAgo = currentMonth.minus(6);
        List<TotalIncomeResponse.ResponseIncome> listRevenue = transactionRepository.totalIncomeSystem();
        
        if(sixMonthAgo.getValue() < currentMonth.getValue()) {
            for(int i = sixMonthAgo.getValue(); i <= currentMonth.getValue(); i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
        } else {
            for(int i = sixMonthAgo.getValue(); i <= 12; i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
            for(int i = 1; i <= currentMonth.getValue(); i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
        }

        BigDecimal totalRevenue = new BigDecimal(0);

        for (TotalIncomeResponse.ResponseIncome detail: listRevenue) {
            totalRevenue = totalRevenue.add(detail.getRevenue());
        }
        response.setTotalRevenueInSixMonth(totalRevenue);
        response.setTotalProfitInSixMonth(totalRevenue.multiply(new BigDecimal(15)).divide(new BigDecimal(100)));
        listRevenue.sort((TotalIncomeResponse.ResponseIncome t1, TotalIncomeResponse.ResponseIncome t2) -> {
            if(t1.getMonthNumber() < t2.getMonthNumber()) return -1;
            return 0;
        });
        response.setDetailIncome(listRevenue);
        return response;
    }

    public TotalBookingWeeklyResponse totalBookingWeeklySystem() {
        TotalBookingWeeklyResponse response = new TotalBookingWeeklyResponse();
        LocalDate today = LocalDate.now();

        // Get first day of current week (Monday for ISO weeks)
        WeekFields weekFields = WeekFields.ISO;
        LocalDate firstDayOfWeek = today.with(weekFields.dayOfWeek(), 1);

        // Create a list to store the days of the week
        List<Integer> daysOfWeek = new ArrayList<>();

        Timestamp monday = null;
        Timestamp sunday = null;

        for (int i = 0; i < 7; i++) {
            if(i == 0) {
                monday = Timestamp.valueOf(firstDayOfWeek.plusDays(i).atStartOfDay());
            }
            if(i == 6) {
                sunday = Timestamp.valueOf(firstDayOfWeek.plusDays(i).atStartOfDay());
            }
            daysOfWeek.add(firstDayOfWeek.plusDays(i).getDayOfMonth());
        }

        List<TotalBookingWeeklyResponse.BookingWeekly> listBooking = bookingRepository.totalBookingByWeeklySystem(monday, sunday);

        //fix
        for(int i = 0; i < daysOfWeek.size(); i++) {
            boolean isExist = false;
            for (TotalBookingWeeklyResponse.BookingWeekly data: listBooking) {
                if(data.getDay() == daysOfWeek.get(i)) {
                    isExist = true;
                }
            }
            if(!isExist) {
                TotalBookingWeeklyResponse.BookingWeekly temp = new TotalBookingWeeklyResponse.BookingWeekly();
                temp.setDay(daysOfWeek.get(i));
                temp.setNumberBooking(0);
                listBooking.add(temp);
            }
        }

        listBooking.sort((TotalBookingWeeklyResponse.BookingWeekly t1, TotalBookingWeeklyResponse.BookingWeekly t2) -> {
            if(t1.getDay() < t2.getDay()) return -1;
            return 0;
        });

        response.setListBookingWeekly(listBooking);
        return response;
    }

    public DataNumberBusinessAdminResponse dataBusinessAdmin() {
        DataNumberBusinessAdminResponse response = new DataNumberBusinessAdminResponse();

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int totalHotel = hotelRepository.totalHotelBusinessAdmin(principal.getId());
        int totalStaff = userRepository.totalStaffBusinessAdmin(principal.getId());
        int totalBooking = bookingRepository.totalBookingBusinessAdmin(principal.getId());
        BigDecimal totalRevenue = transactionRepository.totalRevenueBusinessAdmin(principal.getId());
        if(totalRevenue != null) {
            BigDecimal totalProfit = totalRevenue.multiply(new BigDecimal(85)).divide(new BigDecimal(100));
            response.setTotalProfit(totalProfit);
        }

        response.setTotalHotel(totalHotel);
        response.setTotalStaff(totalStaff);
        response.setTotalBooking(totalBooking);
        response.setTotalRevenue(totalRevenue);
        return response;
    }

    public TotalIncomeResponse totalIncomeBusinessAdmin() {
        TotalIncomeResponse response = new TotalIncomeResponse();
        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        Month sixMonthAgo = currentMonth.minus(6);

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TotalIncomeResponse.ResponseIncome> listRevenue = transactionRepository.totalIncomeBusinessAdmin(principal.getId());

        if(sixMonthAgo.getValue() < currentMonth.getValue()) {
            for(int i = sixMonthAgo.getValue(); i <= currentMonth.getValue(); i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
        } else {
            for(int i = sixMonthAgo.getValue(); i <= 12; i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
            for(int i = 1; i <= currentMonth.getValue(); i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
        }

        BigDecimal totalRevenue = new BigDecimal(0);

        for (TotalIncomeResponse.ResponseIncome detail: listRevenue) {
            totalRevenue = totalRevenue.add(detail.getRevenue());
        }
        response.setTotalRevenueInSixMonth(totalRevenue);
        response.setTotalProfitInSixMonth(totalRevenue.multiply(new BigDecimal(85)).divide(new BigDecimal(100)));
        listRevenue.sort((TotalIncomeResponse.ResponseIncome t1, TotalIncomeResponse.ResponseIncome t2) -> {
            if(t1.getMonthNumber() < t2.getMonthNumber()) return -1;
            return 0;
        });
        response.setDetailIncome(listRevenue);
        return response;
    }

    public TotalBookingWeeklyResponse totalBookingWeeklyBusinessAdmin() {
        TotalBookingWeeklyResponse response = new TotalBookingWeeklyResponse();
        LocalDate today = LocalDate.now();

        // Get first day of current week (Monday for ISO weeks)
        WeekFields weekFields = WeekFields.ISO;
        LocalDate firstDayOfWeek = today.with(weekFields.dayOfWeek(), 1);

        // Create a list to store the days of the week
        List<Integer> daysOfWeek = new ArrayList<>();

        Timestamp monday = null;
        Timestamp sunday = null;

        for (int i = 0; i < 7; i++) {
            if(i == 0) {
                monday = Timestamp.valueOf(firstDayOfWeek.plusDays(i).atStartOfDay());
            }
            if(i == 6) {
                sunday = Timestamp.valueOf(firstDayOfWeek.plusDays(i).atStartOfDay());
            }
            daysOfWeek.add(firstDayOfWeek.plusDays(i).getDayOfMonth());
        }

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TotalBookingWeeklyResponse.BookingWeekly> listBooking = bookingRepository.totalBookingByWeeklyBusinessAdmin(monday, sunday, principal.getId());

        //fix
        for(int i = 0; i < daysOfWeek.size(); i++) {
            boolean isExist = false;
            for (TotalBookingWeeklyResponse.BookingWeekly data: listBooking) {
                if(data.getDay() == daysOfWeek.get(i)) {
                    isExist = true;
                }
            }
            if(!isExist) {
                TotalBookingWeeklyResponse.BookingWeekly temp = new TotalBookingWeeklyResponse.BookingWeekly();
                temp.setDay(daysOfWeek.get(i));
                temp.setNumberBooking(0);
                listBooking.add(temp);
            }
        }

        listBooking.sort((TotalBookingWeeklyResponse.BookingWeekly t1, TotalBookingWeeklyResponse.BookingWeekly t2) -> {
            if(t1.getDay() < t2.getDay()) return -1;
            return 0;
        });

        response.setListBookingWeekly(listBooking);
        return response;
    }

    public DataNumberBusinessOwnerResponse dataBusinessOwner() {
        DataNumberBusinessOwnerResponse response = new DataNumberBusinessOwnerResponse();

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int totalBooking = bookingRepository.totalBookingBusinessOwner(principal.getId());
        BigDecimal totalRevenue = transactionRepository.totalRevenueBusinessOwner(principal.getId());
        if(totalRevenue != null) {
            BigDecimal totalProfit = totalRevenue.multiply(new BigDecimal(85)).divide(new BigDecimal(100));
            response.setTotalProfit(totalProfit);
        }

        response.setTotalBooking(totalBooking);
        response.setTotalRevenue(totalRevenue);
        return response;
    }

    public TotalIncomeResponse totalIncomeBusinessOwner() {
        TotalIncomeResponse response = new TotalIncomeResponse();
        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        Month sixMonthAgo = currentMonth.minus(6);

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TotalIncomeResponse.ResponseIncome> listRevenue = transactionRepository.totalIncomeBusinessOwner(principal.getId());

        if(sixMonthAgo.getValue() < currentMonth.getValue()) {
            for(int i = sixMonthAgo.getValue(); i <= currentMonth.getValue(); i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
        } else {
            for(int i = sixMonthAgo.getValue(); i <= 12; i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
            for(int i = 1; i <= currentMonth.getValue(); i++) {
                boolean isExist = false;
                for (TotalIncomeResponse.ResponseIncome data: listRevenue) {
                    if(data.getMonthNumber() == i) {
                        isExist = true;
                    }
                }
                if(!isExist) {
                    TotalIncomeResponse.ResponseIncome temp = new TotalIncomeResponse.ResponseIncome();
                    temp.setMonthCode(Month.of(i).name());
                    temp.setMonthNumber(i);
                    temp.setRevenue(new BigDecimal(0));
                    temp.setProfit(new BigDecimal(0));
                    listRevenue.add(temp);
                }
            }
        }

        BigDecimal totalRevenue = new BigDecimal(0);

        for (TotalIncomeResponse.ResponseIncome detail: listRevenue) {
            totalRevenue = totalRevenue.add(detail.getRevenue());
        }
        response.setTotalRevenueInSixMonth(totalRevenue);
        response.setTotalProfitInSixMonth(totalRevenue.multiply(new BigDecimal(85)).divide(new BigDecimal(100)));
        listRevenue.sort((TotalIncomeResponse.ResponseIncome t1, TotalIncomeResponse.ResponseIncome t2) -> {
            if(t1.getMonthNumber() < t2.getMonthNumber()) return -1;
            return 0;
        });
        response.setDetailIncome(listRevenue);
        return response;
    }

    public TotalBookingWeeklyResponse totalBookingWeeklyBusinessOwner() {
        TotalBookingWeeklyResponse response = new TotalBookingWeeklyResponse();
        LocalDate today = LocalDate.now();

        // Get first day of current week (Monday for ISO weeks)
        WeekFields weekFields = WeekFields.ISO;
        LocalDate firstDayOfWeek = today.with(weekFields.dayOfWeek(), 1);

        // Create a list to store the days of the week
        List<Integer> daysOfWeek = new ArrayList<>();

        Timestamp monday = null;
        Timestamp sunday = null;

        for (int i = 0; i < 7; i++) {
            if(i == 0) {
                monday = Timestamp.valueOf(firstDayOfWeek.plusDays(i).atStartOfDay());
            }
            if(i == 6) {
                sunday = Timestamp.valueOf(firstDayOfWeek.plusDays(i).atStartOfDay());
            }
            daysOfWeek.add(firstDayOfWeek.plusDays(i).getDayOfMonth());
        }

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TotalBookingWeeklyResponse.BookingWeekly> listBooking = bookingRepository.totalBookingByWeeklyBusinessOwner(monday, sunday, principal.getId());

        //fix
        for(int i = 0; i < daysOfWeek.size(); i++) {
            boolean isExist = false;
            for (TotalBookingWeeklyResponse.BookingWeekly data: listBooking) {
                if(data.getDay() == daysOfWeek.get(i)) {
                    isExist = true;
                }
            }
            if(!isExist) {
                TotalBookingWeeklyResponse.BookingWeekly temp = new TotalBookingWeeklyResponse.BookingWeekly();
                temp.setDay(daysOfWeek.get(i));
                temp.setNumberBooking(0);
                listBooking.add(temp);
            }
        }

        listBooking.sort((TotalBookingWeeklyResponse.BookingWeekly t1, TotalBookingWeeklyResponse.BookingWeekly t2) -> {
            if(t1.getDay() < t2.getDay()) return -1;
            return 0;
        });

        response.setListBookingWeekly(listBooking);
        return response;
    }
}
