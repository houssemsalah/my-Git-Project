package com.itmsd.medical.controllers;
import com.itmsd.medical.payload.request.MailRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({ "/emails" })
@CrossOrigin(origins = "*")
public class MailController {

    private final JavaMailSender mailSender;

    public MailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/send-email")
    public void sendEmail(@RequestBody MailRequest mailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailRequest.getSender());
        message.setTo(mailRequest.getReceiver());
        message.setSubject(  "Message from "+mailRequest.getName()+" (My-Craft-Solutions)");

        message.setText(
                "Sender:   "+mailRequest.getName()+"  "+" ("+mailRequest.getSender()+" ) " +"  \n \n"+
                "Tel:  "+mailRequest.getPhoneNumber()+" \n  \n \n"+
                "Message :  \n \n" + mailRequest.getMessage()
        );

        mailSender.send(message);
    }
}