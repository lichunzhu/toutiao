package com.baine.toutiao.util;

import com.baine.toutiao.dao.KeyDataDAO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    KeyDataDAO keyDataDAO;

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(keyDataDAO.getKeyData().getMailUsername());
        mailSender.setPassword(keyDataDAO.getKeyData().getMailPassword());
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth", true);
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }

    public boolean sendWithHTMLTemplate(String to, String subject, String templatePath,
                                        Map<String, Object> model) {
        try {
            // 设置发送的邮件title
            String nick = MimeUtility.encodeText("chauncy");
            InternetAddress from = new InternetAddress(nick + "<chauncylee@163.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // freemarker template 渲染
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
            configuration.setDefaultEncoding("UTF-8");
            // 注意, 相对路径从resources目录开始!!!
            configuration.setClassLoaderForTemplateLoading(ClassLoader.getSystemClassLoader(), "templates/mails/");
            Template emailTemplate = configuration.getTemplate(templatePath);
            String result = FreeMarkerTemplateUtils.processTemplateIntoString(emailTemplate, model);

            // 发送邮件
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }
}
