package com.satyam.securecapita.infrastructure.service;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.constants.ApplicationUtil;
import com.satyam.securecapita.infrastructure.constants.MessageTemplateEnum;
import com.satyam.securecapita.infrastructure.constants.TwilioConfig;
import com.satyam.securecapita.infrastructure.data.SmsSendingPojo;
import com.satyam.securecapita.infrastructure.message.MessageTemplate;
import com.satyam.securecapita.infrastructure.message.MessageTemplateRepository;
import com.satyam.securecapita.infrastructure.message.MessageTemplateService;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.model.OtpToken;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.OtpTokenRepository;
import com.satyam.securecapita.user.serviceImpl.UserRepositoryWrapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Primary
public class MobileSmsServiceImpl implements SmsService {

    private final TwilioConfig twilioConfig;
    private final OtpTokenRepository otpRepo;
    private final UserRepositoryWrapper userRepositoryWrapper;
    private final MessageTemplateRepository smsTemplateRepo;
    private final MessageTemplateService messageTemplateService;

    @Autowired
    public MobileSmsServiceImpl(TwilioConfig twilioConfig, OtpTokenRepository otpRepo, UserRepositoryWrapper userRepositoryWrapper, MessageTemplateRepository smsTemplateRepo, MessageTemplateService messageTemplateService) {
        this.twilioConfig = twilioConfig;
        this.otpRepo = otpRepo;
        this.userRepositoryWrapper = userRepositoryWrapper;
        this.smsTemplateRepo = smsTemplateRepo;
        this.messageTemplateService = messageTemplateService;
    }

    /*
     *  if message is SUCCESS then we get reqId for otp validation
     *  else error.
     * */
    @Transactional
    @Override
    public SmsSendingPojo sendMessage(User user, MessageTemplateEnum templateEnum, Map<String, String> placeholder) {
        log.error(this.twilioConfig.toString());

        SmsSendingPojo responseObj = new SmsSendingPojo("Issue in sending sms.", null);
        String mobileNo = user.getMobileNumber();
        String templateName = templateEnum.getTemplateName();
        if (Objects.isNull(mobileNo) || mobileNo.isBlank() || mobileNo.length() != 10)
            throw new ApplicationException("mobile number unavailable.");
        final MessageTemplate messageTemplateObj = this.smsTemplateRepo.findByTemplateNameIgnoreCase(templateName).
                filter(messageTemplate -> messageTemplate.isAuthorised() && !messageTemplate.getMessageTemplate().isBlank()).
                orElseThrow(() -> new ApplicationException("Message Template for '" + templateName + "' is either not available or not authorised."));
        String message = this.messageTemplateService.getProcessMessage(messageTemplateObj.getMessageTemplate(), placeholder);
        if (templateEnum.isOtpPurpose()) {
            String otp = ApplicationUtil.generateOtp(ApplicationConstants.OPT_LEN);
            OtpToken otpToken = new OtpToken(otp, user.getId(), templateEnum.getTemplateName());
            message = message.replace("{otp}", otp).
                    replace("{otpValidity}", String.valueOf(ApplicationConstants.OTP_VALIDITY));
//        save in db in "m_otp_token"
            Long reqId = this.otpRepo.save(otpToken).getId();
            responseObj.setRequestId(reqId.toString());
        }
        try {
            Twilio.init(this.twilioConfig.getAccountSid(), this.twilioConfig.getAuthToken());
            Message.creator(new PhoneNumber("+91" + mobileNo), new PhoneNumber(this.twilioConfig.getPhoneNumber()), message).create();
            responseObj.setMessage(ApplicationConstants.SUCCESS);
            log.error("sms sent to {} :{}",mobileNo,message);
        } catch (Exception e) {
            log.error("Excepting in sending sms :" + e.getMessage());
            throw new RuntimeException("error in sending sms.");
        }
        return responseObj;
    }

    @Override
    public String validateOtpToken(Long reqId, String otp) {
        OtpToken otpToken = this.otpRepo.findById(reqId).orElseThrow(() -> new ApplicationException("Invalid request, please try again."));
        if (otpToken.getFailedAttemptCount() == ApplicationConstants.OTP_ATTEMPT_COUNT) { //otp attempt expired
            return "Otp attempts exhausted, please clink on resend otp.";
        } else if (otpToken.isOtpExpired()) { //otp time expires
            return "Otp time limit exceeds, please clink on resend otp.";
        } else if (!otpToken.getOtpNumber().trim().equals(otp.trim())) { // invalid otp
//            increment attempt count by 1
            otpToken.wrongOtpAttempt();
            this.otpRepo.save(otpToken);
            return "Invalid otp.";
        } else {
            return ApplicationConstants.SUCCESS;
        }
    }
}
