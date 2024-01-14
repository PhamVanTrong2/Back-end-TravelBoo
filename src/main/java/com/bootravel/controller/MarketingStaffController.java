package com.bootravel.controller;

import com.bootravel.payload.requests.commonRequests.MailsRequests;
import com.bootravel.service.common.EmailService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/staff-marketing")
public class MarketingStaffController {
    @Autowired
    private EmailService emailService;
    @Autowired
    public MarketingStaffController(EmailService emailService) {
        this.emailService = emailService;
    }
    @PostMapping("/send-mail-marketing")
    public String sendMailMarketing(@RequestBody MailsRequests user, HttpServletRequest request)
            throws IOException, MessagingException {
        var siteUrl = emailService.getSiteURL(request);
        log.info("Site URL: {}", siteUrl);
        emailService.sendEmailMarketing(user, siteUrl);
        return "SEND SUCCESS";
    }

}
