package com.yourcompany.crm.service;

import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.yourcompany.crm.model.Interaction;
import com.yourcompany.crm.model.Opportunity;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process(template, context);
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendOpportunityStatusUpdate(Opportunity opportunity) {
        Map<String, Object> variables = Map.of(
            "customerName", opportunity.getCustomer().getName(),
            "opportunityTitle", opportunity.getTitle(),
            "status", opportunity.getStatus(),
            "value", opportunity.getValue()
        );

        sendEmail(
            opportunity.getCustomer().getEmail(),
            "Opportunity Status Update",
            "opportunity-status",
            variables
        );
    }

    public void sendFollowUpReminder(Interaction interaction) {
      Map<String, Object> variables = Map.of(
          "customerName", interaction.getCustomer().getName(),
          "interactionType", interaction.getType(),
          "dueDate", interaction.getFollowupDate()
      );
  
      sendEmail(
          interaction.getCustomer().getAssignedUser().getEmail(),  // Modificado aqui
          "Follow-up Reminder",
          "followup-reminder",
          variables
      );
  }
}