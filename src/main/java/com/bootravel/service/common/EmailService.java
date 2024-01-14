package com.bootravel.service.common;

import com.bootravel.repository.BookingRepository;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.entity.BookingRoomsEntity;
import com.bootravel.entity.UsersEntity;
import com.bootravel.entity.enumeration.RecipientTypeMails;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.commonRequests.MailsRequests;
import com.bootravel.repository.HotelRepository;
import com.bootravel.repository.RoomsRepository;
import com.bootravel.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class EmailService {
    private final JavaMailSender mailSender;

    private final UserRepository usersRepository;

    private final BookingRepository bookingRepository;

    private final RoomsRepository roomsRepository;

    private final HotelRepository hotelRepository;

    private final String ENTITY_NAME = "EmailService";

    @Value("${mail.username}")
    private String username;

    @Autowired
    public EmailService(JavaMailSender mailSender, UserRepository usersRepository, BookingRepository bookingRepository, RoomsRepository roomsRepository, HotelRepository hotelRepository) {
        this.mailSender = mailSender;
        this.usersRepository = usersRepository;
        this.bookingRepository = bookingRepository;
        this.roomsRepository = roomsRepository;
        this.hotelRepository = hotelRepository;
    }

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    public void sendEmail(String fileName, String recipientEmail, String subject, File fileAttach, Map<String, String> replacements) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            String templateContent = readFileHtml(fileName);

            if (Objects.nonNull(replacements)) {
                templateContent = replacePlaceholders(templateContent, replacements);
            }

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(templateContent, true);

            if (Objects.nonNull(fileAttach)) {
                helper.addAttachment(fileAttach.getName(), fileAttach);
            }

            mailSender.send(message);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private String readFileHtml(String fileName) throws IOException {
        Context context = new Context();
        return springTemplateEngine.process(fileName, context);
    }

    private String readTemplateFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        }

        return content.toString();
    }

    private String replacePlaceholders(String template, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String placeholder = entry.getKey();
            String value = entry.getValue();
            template = template.replace(placeholder, value);
        }

        return template;
    }

    public List<UsersEntity> findRecipientUsers(RecipientTypeMails recipientType) {
        List<UsersEntity> users = Collections.emptyList(); // Initialize with an empty list

        switch (recipientType) {
            case BUSINESS_ADMIN:
                users = usersRepository.findUserByRoleId(MasterDataConstants.ROLE_BUSINESS_ADMIN);
                break;
            case BUSINESS_OWNER:
                users = usersRepository.findUserByRoleId(MasterDataConstants.ROLE_BUSINESS_OWNER);
                break;
            case ALL:
                users = usersRepository.getAllUser();
                break;
            case USER:
                users = usersRepository.findUserByRoleId(MasterDataConstants.ROLE_REGISTERED_USER);
                break;
            default:
                // Handle any other cases
        }

        if (users.isEmpty()) {
            throw new BadRequestAlertException("Not found", ENTITY_NAME, "NOT_FOUND");
        }

        return users;
    }

    public void sendEmailMarketing(MailsRequests requests, String siteURL) throws MessagingException, IOException {
        List<UsersEntity> users = findRecipientUsers(requests.getTypeMails());

        for (UsersEntity mail : users) {
            String toAddress = mail.getEmail();
            String fromAddress = username;
            String senderName = requests.getName();
            String subject = requests.getTitle();

            // Read the HTML template content from a file
            String content = readTemplateFile("template/mail/TEMPLATE_EMAIL.html");

            // Replace placeholders with dynamic content

            content = content.replace("[[title]]", requests.getTitle());
            content = content.replace("[[content]]", requests.getContent());
            content = content.replace("[[name]]", mail.getFirstName() + " " + mail.getLastName());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        }
    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    public void sendEmailQr(MailsRequests requests, String siteURL, BookingRoomsEntity qrCode) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String toAddress = qrCode.getEmail();
        String fromAddress = username;
        String senderName = requests.getName();
        String subject = requests.getTitle();

        // Read the HTML template content from a file
        String content = readTemplateFile("template/mail/TEMPLATE_EMAIL_QR.html");

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        String formattedDateIn = outputDateFormat.format(qrCode.getCheckin());
        String formattedDateOut = outputDateFormat.format(qrCode.getCheckout());

        // Format LocalDateTime

        DateTimeFormatter outputDateFormats = DateTimeFormatter
                .ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        String timeBookingDateOut = qrCode.getTimeBooking().format(outputDateFormats);

        var roomId = bookingRepository.getBookingRoomDetailsByBookingRoomId(qrCode.getId());
        var nameOfRoom = roomsRepository.getRoomById(roomId.getRoomId());
        var nameOfHotel = hotelRepository.getHotelByRoomId(roomId.getRoomId());
        hotelRepository.updateLastUpdateByHotelId(nameOfHotel.getId());

        String totalBill = String.valueOf(qrCode.getTotalPrice().intValue());

        // Replace placeholders with dynamic content
        content = content.replace("[[name]]", qrCode.getFirstName() + " " + qrCode.getLastName());
        content = content.replace( "[[check-in]]", formattedDateIn);
        content = content.replace("[[check-out]]", formattedDateOut);
        content = content.replace("[[hotel]]", nameOfHotel.getName());
        content = content.replace("[[phone]]", hotelRepository.getHotelPhoneNumberById(nameOfHotel.getId()));
        content = content.replace("[[room]]", nameOfRoom.getName());
        content = content.replace("[[total-bill]]", totalBill);
        content = content.replace("[[time-booking]]",timeBookingDateOut);
        // Generate the QR code image
        Date expirationDate = qrCode.getCheckout(); // Use checkout time as expiration
        BufferedImage qrCodeImage = generateQRCodeImage(qrCode, 200, 200);


        // Attach the QR code image to the email
        helper.addAttachment("qr-code.png", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", outputStream);
            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(outputStream.toByteArray(), "image/png");
            return byteArrayDataSource.getInputStream(); // Convert to InputStream
        });

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
    private static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
    }


    public BufferedImage generateQRCodeImage(BookingRoomsEntity bookingEntity, int width, int height) throws Exception {
        // Create an ObjectMapper with JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Set the desired timezone (Asia/Ho_Chi_Minh)
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        objectMapper.setTimeZone(timeZone);

        // Convert BookingRoomsEntity to JSON
        String bookingJson = objectMapper.writeValueAsString(bookingEntity);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(bookingJson, BarcodeFormat.QR_CODE, width, height);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return bufferedImage;
    }


}
