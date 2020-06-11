package ysf.app.dct.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ysf.app.dct.lib.DCT;
import ysf.app.dct.util.ImageUtils;

@Controller
public class HomeController {

    @Autowired
    ImageUtils imgUtils;

    @Autowired
    DCT dct;

    private static String UPLOADED_FOLDER = "/tmp/";
    private static String FULL_PATH;

    @PostConstruct
    public void initialize() {
        this.FULL_PATH = Paths.get("").toAbsolutePath().toString() + UPLOADED_FOLDER;
//        System.out.println("FULL PATH: " + FULL_PATH);
    }

    @RequestMapping(value={"", "/", "home"}, method = RequestMethod.GET)
    public String main(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }

    @GetMapping("/exetest")
    public String exeTest() {
        dct.funcTest();
        return "home";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file") MultipartFile uploadfile, RedirectAttributes redirectAttributes) {
        if (uploadfile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:home";
        }

        BufferedImage outputImg = null;

        try {
            // Get a BufferedImage object from a byte array
            InputStream in = new ByteArrayInputStream(uploadfile.getBytes());
            BufferedImage originalImage = ImageIO.read(in);

            outputImg = dct.DCTdenoising(originalImage);
        }
        catch (IOException e) {
            System.out.println("Exception when DCT process");
            e.printStackTrace();
        }

        try {
            String outpath = imgUtils.SaveBufImagePNG(outputImg);
            redirectAttributes.addFlashAttribute("output_message","File output saved at '" + outpath + "'");
        } catch (IOException e) {
            System.out.println("Failed save output image to file");
            e.printStackTrace();
        }

        try {
            byte[] bytes = uploadfile.getBytes();
            String filePath = FULL_PATH + uploadfile.getOriginalFilename();
            Path path = Paths.get(filePath);
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("upload_message","File image uploaded '" + filePath + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/result";
    }

}
