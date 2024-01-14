package com.bootravel.payload.requests.commonRequests;

import com.bootravel.entity.enumeration.RecipientTypeMails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailsRequests {
    private String name;
    private String title;
    private String content;
    private RecipientTypeMails typeMails;
}
