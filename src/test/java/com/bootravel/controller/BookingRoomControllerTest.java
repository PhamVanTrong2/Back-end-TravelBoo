package com.bootravel.controller;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.BookingRoomDetailsRequest;
import com.bootravel.payload.requests.BookingRoomRequest;
import com.bootravel.payload.requests.TransactionRequest;
import com.bootravel.service.BookingService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class BookingRoomControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingRoomController bookingRoomController;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_create_booking_room_successfully() throws Exception {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        request.setEmail("test@example.com");
        request.setPhoneNumber("0912345678");
        request.setCheckin(new Date()); // Set a valid check-in date
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        HttpServletRequest requests = mock(HttpServletRequest.class);
        // Act
        ResponseEntity<?> response = bookingRoomController.createBookingRoom(request, requests);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(bookingService, times(1)).createBooking(request, requests);
    }

    @Test
    public void test_create_booking_with_invalid_email_and_phone_number() throws Exception {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        request.setEmail("invalid-email");
        request.setPhoneNumber("invalid-phone-number");
        request.setCheckin(new Date()); // Set a valid check-in date
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details


        HttpServletRequest requests = mock(HttpServletRequest.class);
        BookingService bookingService = mock(BookingService.class);

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        Mockito.when(bookingService.createBooking(request, requests))
                .thenThrow(new BadRequestAlertException("Phone or email invalid ! ", "ENTITY_NAME", "INVALID"));

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act
        try {

            // The line above should throw an exception, so the code below should not be reached
            assertEquals(null, bookingRoomController.createBookingRoom(request, requests));
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Phone or email invalid ! ", e.getMessage());
        }
    }

    @Test
    public void test_create_booking_with_invalid_email() throws Exception {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        request.setEmail("invalid-email");
        request.setPhoneNumber("0912345678");
        request.setCheckin(new Date()); // Set a valid check-in date
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        HttpServletRequest requests = mock(HttpServletRequest.class);
        BookingService bookingService = mock(BookingService.class);

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        Mockito.when(bookingService.createBooking(request, requests))
                .thenThrow(new BadRequestAlertException("Phone or email invalid ! ", "ENTITY_NAME", "INVALID"));

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act
        try {

            // The line above should throw an exception, so the code below should not be reached
            assertEquals(null, bookingRoomController.createBookingRoom(request, requests));
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Phone or email invalid ! ", e.getMessage());
        }
    }

    @Test
    public void test_create_booking_with_invalid_phone() throws Exception {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        request.setEmail("test@gmail.com");
        request.setPhoneNumber("invalid-phone-number");
        request.setCheckin(new Date()); // Set a valid check-in date
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        HttpServletRequest requests = mock(HttpServletRequest.class);
        BookingService bookingService = mock(BookingService.class);

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        Mockito.when(bookingService.createBooking(request, requests))
                .thenThrow(new BadRequestAlertException("Phone or email invalid ! ", "ENTITY_NAME", "INVALID"));

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act
        try {

            // The line above should throw an exception, so the code below should not be reached
            assertEquals(null, bookingRoomController.createBookingRoom(request, requests));
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Phone or email invalid ! ", e.getMessage());
        }
    }

    @Test
    public void test_create_booking_checkin_before_current_date() {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        HttpServletRequest requests = mock(HttpServletRequest.class);

        request.setEmail("test@gmail.com");
        request.setPhoneNumber("invalid-phone-number");
        // Set check-in date before current date for request object
        request.setCheckin(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        try {
            Mockito.when(bookingService.createBooking(request, requests))
                    .thenThrow(new BadRequestAlertException("Check-in date invalid ! ", "BookingService", "INVALID"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act & Assert
        try {
            bookingRoomController.createBookingRoom(request, requests);
            // The line above should throw an exception, so the code below should not be reached
            Assert.fail("Expected BadRequestAlertException was not thrown");
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Check-in date invalid ! ", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_create_booking_checkout_before_checkin_date() {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        HttpServletRequest requests = mock(HttpServletRequest.class);

        // Set check-out date before check-in date for request object
        request.setCheckin(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        request.setCheckout(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())); // Set check-out before check-in

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException

        // Act

        try {
            Mockito.when(bookingService.createBooking(request, requests))
                    .thenThrow(new BadRequestAlertException("Check-in date invalid ! ", "BookingService", "INVALID"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act & Assert
        try {
            // The line above should throw an exception, so the code below should not be reached
            assertEquals("<200 OK OK,[]>",  bookingRoomController.createBookingRoom(request, requests));
            // Assert

        } catch (Exception e) {
            assertEquals("Check-in date invalid ! ", e.getMessage());
        }
    }

    @Test
    public void test_create_booking_room_successfully_no_promotion() throws Exception {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        request.setEmail("test@example.com");
        request.setPhoneNumber("0912345678");
        request.setCheckin(new Date()); // Set a valid check-in date
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        HttpServletRequest requests = mock(HttpServletRequest.class);
        // Act
        ResponseEntity<?> response = bookingRoomController.createBookingRoom(request, requests);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(bookingService, times(1)).createBooking(request, requests);
    }

    @Test
    public void test_create_booking_checkin_null() {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        HttpServletRequest requests = mock(HttpServletRequest.class);

        request.setEmail("test@gmail.com");
        request.setPhoneNumber("0912345678");
        // Set check-in date before current date for request object
        //request.setCheckin(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        try {
            Mockito.when(bookingService.createBooking(request, requests))
                    .thenThrow(new BadRequestAlertException("Check-in date invalid ! ", "BookingService", "INVALID"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act & Assert
        try {
            bookingRoomController.createBookingRoom(request, requests);
            // The line above should throw an exception, so the code below should not be reached
            Assert.fail("Expected BadRequestAlertException was not thrown");
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Check-in date invalid ! ", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_create_booking_checkout_null() {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        HttpServletRequest requests = mock(HttpServletRequest.class);

        request.setEmail("test@gmail.com");
        request.setPhoneNumber("0912345678");
        request.setCheckin(new Date());
        request.setCheckout(new Date("32/12/2023"));
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        try {
            Mockito.when(bookingService.createBooking(request, requests))
                    .thenThrow(new BadRequestAlertException("Check-out date invalid ! ", "BookingService", "INVALID"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act & Assert
        try {
            bookingRoomController.createBookingRoom(request, requests);
            // The line above should throw an exception, so the code below should not be reached
            Assert.fail("Expected BadRequestAlertException was not thrown");
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Check-out date invalid ! ", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_create_booking_checkin_invalid() {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        HttpServletRequest requests = mock(HttpServletRequest.class);

        request.setEmail("test@gmail.com");
        request.setPhoneNumber("invalid-phone-number");
        // Set check-in date before current date for request object
        request.setCheckin(new Date("31/11/2023"));
        request.setCheckout(new Date()); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        try {
            Mockito.when(bookingService.createBooking(request, requests))
                    .thenThrow(new BadRequestAlertException("Check-in date invalid ! ", "BookingService", "INVALID"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act & Assert
        try {
            bookingRoomController.createBookingRoom(request, requests);
            // The line above should throw an exception, so the code below should not be reached
            Assert.fail("Expected BadRequestAlertException was not thrown");
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Check-in date invalid ! ", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_create_booking_checkout_invalid() {
        // Arrange
        BookingRoomRequest request = new BookingRoomRequest();
        HttpServletRequest requests = mock(HttpServletRequest.class);

        request.setEmail("test@gmail.com");
        request.setPhoneNumber("invalid-phone-number");
        // Set check-in date before current date for request object
        request.setCheckin(new Date());
        request.setCheckout(new Date("32/12/2023")); // Set a valid check-out date
        request.setNote("Test note");
        request.setPromotionCode("PROMO123");
        request.setLastName("Doe");
        request.setFirstName("John");
        request.setTotalDay(5); // Set a valid total day
        request.setBookingRoomDetailsEntity(new BookingRoomDetailsRequest()); // Set valid booking room details
        request.setTransactionsEntity(new TransactionRequest()); // Set valid transaction details

        // Mock the behavior of bookingService.createBooking to throw a BadRequestAlertException
        try {
            Mockito.when(bookingService.createBooking(request, requests))
                    .thenThrow(new BadRequestAlertException("Check-out date invalid ! ", "BookingService", "INVALID"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BookingRoomController bookingRoomController = new BookingRoomController(bookingService);

        // Act & Assert
        try {
            bookingRoomController.createBookingRoom(request, requests);
            // The line above should throw an exception, so the code below should not be reached
            Assert.fail("Expected BadRequestAlertException was not thrown");
        } catch (BadRequestAlertException e) {
            // Assert
            assertEquals("Check-out date invalid ! ", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
