package com.example.dcm_test.controller;

import com.aspose.imaging.Image;
import com.aspose.imaging.fileformats.dicom.DicomImage;
import com.aspose.imaging.imageoptions.JpegOptions;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;


@Controller
@RequestMapping("/dicom")
public class DicomController {
    @GetMapping("/")
    public String showUploadForm() {
        return "uploadForm"; // Returns the Thymeleaf template name (uploadForm.html)
    }

    @PostMapping("/upload")
    public String uploadDicomFile(@RequestParam("file") MultipartFile dicomFileMultipart, Model model) {
        try {
            String fileName = dicomFileMultipart.getOriginalFilename();
            String uploadDirectory = "src/main/resources/saving";
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File destinationFile = new File(directory.getAbsolutePath() + File.separator + fileName);
            dicomFileMultipart.transferTo(destinationFile);

            DicomInputStream dis = new DicomInputStream(destinationFile);
            Attributes metadata = dis.readDataset(-1, -1);
            dis.close();

            // You can now access the metadata using the get() method of the Attributes object.
            // For example, to get the patient's name, you can use:
            String patientName = metadata.getString(Tag.PatientName);
            System.out.println("Patient name: " + patientName);
            model.addAttribute("message", metadata);
            return "exampleTemplate";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing DICOM file: " + e.getMessage();
        }
    }

    @GetMapping("/convertDiComToJpeg")
    public void displayDicomImage() {

        DicomImage dicomImage = (DicomImage) Image.load("src/main/resources/dicomFile/1.3.46.670589.5.2.10.2156913941.892665339.860724_0001_002000_14579035620000.dcm");

        // Définir la page active à convertir en JPEG
        dicomImage.setActivePage(dicomImage.getDicomPages()[0]);

        JpegOptions jpegOptions = new JpegOptions();

        // Enregistrer au format JPEG
        dicomImage.save("src/main/resources/Output/DICOM_to_JPEG.jpg", jpegOptions);
    }
}
