package com.iaasimov.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import play.Configuration;
import play.Play;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class ImageUtils {
	public static String generateOfferImage(String host, String imgUrl, String strOffer) {
		try {
			URL url = new URL(imgUrl);
	        BufferedImage im = ImageIO.read(url);
	        im = scale(im, 640, 480);
	        
	        BufferedImage im2 = ImageIO.read(Play.application().resourceAsStream("public/images/img/specialdealsleft.png"));
	        im2 = scale(im2, 193, 155);
	        
	        Graphics2D g = im.createGraphics();
	        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	        g.drawImage(im2, 0, im2.getHeight()-80, null);
	        
	        g.setPaint(new Color(255, 170, 0));
	        g.drawRoundRect(10, im.getHeight()/2+135, strOffer.length()*8, 20, 5, 5);
	        g.fillRoundRect(10, im.getHeight()/2+135, strOffer.length()*8, 20, 5, 5);
	        
	        g.setPaint(Color.white);
	        g.setFont(new Font("helvetica", Font.BOLD, 12));
	        g.drawString(strOffer, 15, im.getHeight()/2+150);
	        g.dispose();
	        
//	        String fileName = url.getFile();
//	        fileName = fileName.substring(fileName.lastIndexOf("/")).replaceAll(":", "_");
//	        File offerImage = new File("public/images/offers/" + fileName);
//            FileOutputStream fileOuputStream = new FileOutputStream(offerImage);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        	ImageIO.write(im, "jpg", baos);
//        	baos.flush();
//        	byte[] imageInByte = baos.toByteArray();
//        	fileOuputStream.write(imageInByte);
//        	baos.close();
//            fileOuputStream.close();
//	        return host +"assets/images/offers/"+fileName;
	        
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	ImageIO.write(im, "jpg", baos);
			
	    	Configuration configuration  = Play.application().configuration();
			Cloudinary cloudinary = new Cloudinary();
			Map uploadResult = cloudinary.uploader().upload(baos.toByteArray(), ObjectUtils.asMap("cloud_name", configuration.getString("cloudinary.cloud_name"),
	  			  																					"api_key", configuration.getString("cloudinary.api_key"),
	  			  																					"api_secret", configuration.getString("cloudinary.api_secret")));
			return ""+uploadResult.get("secure_url");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return imgUrl;
	}
	
	private static BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }
}
