package ysf.app.dct.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ysf.app.dct.util.StorageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class ResultController {

    private StorageUtil stoUtil;

    @Autowired
    public ResultController(StorageUtil stoUtil) {
        this.stoUtil = stoUtil;
    }

    @GetMapping("/result")
    public String result() {
        return "result";
    }

    @RequestMapping(value = "uploads/{imageName}")
    @ResponseBody
    public byte[] getOriImage(@PathVariable(value = "imageName") String imageName) throws IOException {

        String uploadPath = stoUtil.getUploadsPath();
        File serverFile = new File(uploadPath + "/" + imageName);

        return Files.readAllBytes(serverFile.toPath());
    }

    @RequestMapping(value = "results/{imageName}")
    @ResponseBody
    public byte[] getResultImage(@PathVariable(value = "imageName") String imageName) throws IOException {

        String resultPath = stoUtil.getResultsPath();
        File serverFile = new File(resultPath + "/" + imageName);

        return Files.readAllBytes(serverFile.toPath());
    }
}
