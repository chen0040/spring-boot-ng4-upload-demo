package com.github.chen0040.bootslingshot.controllers;

import com.github.chen0040.bootslingshot.services.AccountApi;
import com.github.chen0040.bootslingshot.services.BinaryObjApi;
import com.github.chen0040.bootslingshot.utils.Helpers;
import com.github.chen0040.bootslingshot.utils.ResourceFileUtils;
import com.github.chen0040.bootslingshot.utils.StringUtils;
import com.github.chen0040.bootslingshot.viewmodels.BinaryObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;
import com.github.chen0040.bootslingshot.viewmodels.UsernameAndToken;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Controller
public class BinaryObjController {

    private static final Logger logger = LoggerFactory.getLogger(BinaryObjController.class);

    @Autowired
    private BinaryObjApi service;

    @Autowired
    private AccountApi accountApi;

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    @RequestMapping(value="/erp/binary-obj/find-all-ids-by-tag", method= RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public List<Long> findAllIdsByTag(@RequestParam("tag") String tag, @RequestBody TokenObj tokenObj) {
        return service.findAllIdsByTag(tag, tokenObj);
    }


    @RequestMapping(value="/erp/binary-obj/find-binary-obj-by-id", method = RequestMethod.POST, consumes = "application/json")
    public
    void findBinaryObjectById(@RequestParam("id") long id, @RequestBody TokenObj tokenObj, HttpServletResponse response) throws IOException {
        BinaryObj banner = service.findById(id, tokenObj);

        if(StringUtils.isEmpty(banner.getError())){
            String base64 = banner.getModel();
            byte[] bytes = Base64.getDecoder().decode(base64);
            if (bytes != null) {
                response.setContentType("application/octet-stream");
                response.getOutputStream().write(bytes);
                response.getOutputStream().close();
            }
        } else {
            byte[] bytes = ResourceFileUtils.getBytes("images/placeholder.png");
            if (bytes != null) {
                response.setContentType("application/octet-stream");
                response.getOutputStream().write(bytes);
                response.getOutputStream().close();
            }
        }
    }

    @RequestMapping(value="/erp/binary-obj/delete-by-id", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody BinaryObj deleteById(@RequestParam("id") long id, @RequestBody UsernameAndToken uat) {
        final String token = uat.getToken();
        final TokenObj tokenObj = new TokenObj();
        tokenObj.setToken(token);
        BinaryObj bo = service.findById(id, tokenObj);
        if(StringUtils.isEmpty(bo.getTag())){
            return bo;
        }
        if(bo.getTag().contains(uat.getUsername())) {
            return service.deleteById(id, tokenObj);
        }
        return bo;
    }


    @RequestMapping(value = "/erp/binary-obj/upload", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> uploadBanner(@RequestParam("id") long id,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestParam("secret") String secret,
                                     @RequestParam("tag") String tag,
                                     @RequestParam("token") String token)
            throws ServletException, IOException {

        Map<String, Object> result = new HashMap<>();

        UsernameAndToken account = accountApi.validateToken(token);

        if(!Helpers.isValidAccount(account)) {
            result.put("success", false);
            result.put("error", "Invalid Token");
            result.put("id", "");
            return result;
        }

        if(!secret.equals("wizlah")){
            result.put("success", false);
            result.put("error", "Invalid Secret");
            result.put("id", "");
            return result;
        }

        try {
            byte[] bytes = file.getBytes();
            final String base64Data = Base64.getEncoder().encodeToString(bytes);

            final BinaryObj bo = new BinaryObj();
            bo.setModel(base64Data);
            bo.setToken(token);
            bo.setId(id);
            bo.setTag(tag);

            logger.info("image bytes received: {}", bytes.length);

            executor.submit(() -> {
                service.save(bo);
            });

            result.put("success", true);
            result.put("id", id);
            result.put("error", "");

            return result;
        }catch(IOException ex) {
            logger.error("Failed to process the uploaded image", ex);
            result.put("success", false);
            result.put("id", "");
            result.put("error", ex.getMessage());
            return result;
        }
    }


}
