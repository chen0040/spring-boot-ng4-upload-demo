package com.github.chen0040.bootslingshot.controllers;

import com.github.chen0040.bootslingshot.services.AccountApi;
import com.github.chen0040.bootslingshot.services.ImageObjApi;
import com.github.chen0040.bootslingshot.utils.Helpers;
import com.github.chen0040.bootslingshot.utils.ResourceFileUtils;
import com.github.chen0040.bootslingshot.utils.StringUtils;
import com.github.chen0040.bootslingshot.viewmodels.ImageObj;
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
public class ImageObjController {

    private static final Logger logger = LoggerFactory.getLogger(ImageObjController.class);

    @Autowired
    private ImageObjApi service;

    @Autowired
    private AccountApi accountApi;

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    @RequestMapping(value="/erp/image-obj/find-all-ids-by-tag", method= RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public List<Long> findAllIdsByTag(@RequestParam("tag") String tag, @RequestBody TokenObj tokenObj) {
        return service.findAllIdsByTag(tag, tokenObj);
    }

    @RequestMapping(value="/erp/image-obj/find-first-image-obj-by-parent-id", method = RequestMethod.POST, consumes = "application/json")
    public
    void findFirstImageObjectByParentId(@RequestParam("parentId") long parentId, @RequestBody TokenObj tokenObj, HttpServletResponse response) throws IOException {
        ImageObj banner = service.findFirstByParentId(parentId, tokenObj);

        if(StringUtils.isEmpty(banner.getError())){
            String base64 = banner.getModel();
            byte[] bytes = Base64.getDecoder().decode(base64);
            if (bytes != null) {
                response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
                response.getOutputStream().write(bytes);
                response.getOutputStream().close();
            }
        } else {
            byte[] bytes = ResourceFileUtils.getBytes("images/placeholder.png");
            if (bytes != null) {
                response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
                response.getOutputStream().write(bytes);
                response.getOutputStream().close();
            }
        }
    }

    @RequestMapping(value="/erp/image-obj/find-image-obj-by-id", method = RequestMethod.POST, consumes = "application/json")
    public
    void findImageObjectById(@RequestParam("id") long id, @RequestBody TokenObj tokenObj, HttpServletResponse response) throws IOException {
        ImageObj banner = service.findById(id, tokenObj);

        if(StringUtils.isEmpty(banner.getError())){
            String base64 = banner.getModel();
            byte[] bytes = Base64.getDecoder().decode(base64);
            if (bytes != null) {
                response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
                response.getOutputStream().write(bytes);
                response.getOutputStream().close();
            }
        } else {
            byte[] bytes = ResourceFileUtils.getBytes("images/placeholder.png");
            if (bytes != null) {
                response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
                response.getOutputStream().write(bytes);
                response.getOutputStream().close();
            }
        }
    }

    @RequestMapping(value = "/erp/image-obj/upload", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> uploadImageObject(@RequestParam("id") long id,
                                     @RequestParam("parentId") long parentId,
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

        if(!secret.equals("secret")){
            result.put("success", false);
            result.put("error", "Invalid Secret");
            result.put("id", "");
            return result;
        }

        try {
            byte[] bytes = file.getBytes();
            final String base64Data = Base64.getEncoder().encodeToString(bytes);

            final ImageObj bo = new ImageObj();
            bo.setModel(base64Data);
            bo.setToken(token);
            bo.setId(id);
            bo.setTag(tag);
            bo.setParentId(parentId);

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
