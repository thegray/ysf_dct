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
import ysf.app.dct.lib.DCTBasisMode;
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
    public String singleFileUpload(@RequestParam("file") MultipartFile uploadfile, String sigma, RedirectAttributes redirectAttributes) {
        if (uploadfile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:home";
        }

        int sigmaValue = 0;
        try {
            sigmaValue = Integer.parseInt(sigma);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Error sigma not integer");
            redirectAttributes.addFlashAttribute("message", "Sigma value should integer");
            return "redirect:home";
        }

        BufferedImage outputImg = null;

        try {
            // Get a BufferedImage object from a byte array
            InputStream in = new ByteArrayInputStream(uploadfile.getBytes());
            BufferedImage originalImage = ImageIO.read(in);

            outputImg = dct.DCTdenoising(originalImage, sigmaValue, DCTBasisMode.Mode16);
        }
        catch (IOException e) {
            System.out.println("Error when try to save output");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception happened at DCTdenoising");
            e.printStackTrace();
        }

        String outputImageName = null;
        try {
            String fullOutputPath = imgUtils.SaveBufImagePNG(outputImg, outputImageName);
            redirectAttributes.addFlashAttribute("output_message","File output saved at '" + fullOutputPath + "'");
        } catch (IOException e) {
            System.out.println("Failed save output image to file");
            e.printStackTrace();
        }

        String uploadedName = null;
        try {
            byte[] bytes = uploadfile.getBytes();
            uploadedName = uploadfile.getOriginalFilename();
            String filePath = FULL_PATH + uploadedName;
            Path path = Paths.get(filePath);
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("upload_message","File image uploaded '" + filePath + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        File output saved at '/Users/paulus.bangun/pbk/repo/expr/dct_prj/./output/output_18434341872020953707.png'
//        File image uploaded '/Users/paulus.bangun/pbk/repo/expr/dct_prj/tmp/test_222.png'
        redirectAttributes.addFlashAttribute("output_image_name",outputImageName);
        redirectAttributes.addFlashAttribute("uploaded_name",FULL_PATH + uploadedName);


        return "redirect:/result";
    }

}
