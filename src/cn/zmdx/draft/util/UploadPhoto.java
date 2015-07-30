package cn.zmdx.draft.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.tencent.cos.Cos;
import com.tencent.cos.CosImpl;
import com.tencent.cos.bean.CosFile;
import com.tencent.cos.bean.Message;
import com.tencent.cos.constant.Common;

public class UploadPhoto {

	/**
	 * 上传用户照片
	 * @author louxiaojian
	 * @date： 日期：2015-6-5 时间：下午3:47:10
	 * @param fis
	 * @param photoName
	 * @return
	 * @throws Exception
	 */
	public static String uploadPhoto(FileInputStream fis,String photoName) {
		try{
			BufferedImage jgp = ImageIO.read(fis);
			// 用户定义变量
			int accessId = 11000436; // accessId
			String accessKey = "7OgnLklEIptHNwZCS0RDNk1rUXrxXJfP"; // accessKey
			String bucketId = "data"; // bucket id
			String secretId = "AKIDBvY9dcNUS2LeFTxI2ThzgrKxuWuNROIr";
			Cos cos = null;
			try {
				cos = new CosImpl(accessId, accessKey, Common.COS_HOST,
						Common.DOWNLOAD_HOST, secretId);
			} catch (Exception e) {
	//				logger.error(e);
				e.printStackTrace();
			}
			String imgType = "";
			String uploadImg = "";
			if (null != photoName && !"".equals(photoName)) {
				imgType = photoName.substring(photoName.lastIndexOf("."));
				uploadImg = photoName + new Date().getTime();
				MD5 md5 = new MD5();
				uploadImg = md5.getMD5ofStr(uploadImg);
			}
	
			String fileName = uploadImg + imgType;
	//			float rate;
			int height=jgp.getHeight();
			int width=jgp.getWidth();
	//			int minusWidth=(jgp.getWidth()-2000)/2;
			//上传 xh 分辨率 原图
			BufferedImage inputbig = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
	//    	    inputbig.getGraphics().drawImage(jgp,0,0, width, height, minusWidth,0,width-minusWidth,height, null); //画图
		    inputbig.getGraphics().drawImage(jgp,0,0, width, height, null); //画图
		    ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(inputbig, "jpg", os);
			Message msg = new Message();
			// System.out.println("----------------------uploadFileContent----------------------\n");
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put("compressBucketId", bucketId);
			inParams.put("compressFilePath", "/test/" + fileName);
			Map<String, CosFile> files = new HashMap<String, CosFile>();
			files.put("compressFile", new CosFile());
			cos.uploadFileContentWithCompress(inParams,
					os.toByteArray(), files, msg);
			System.out.println(files);
			System.out.println(msg);
	//    		//生成 xh 分辨率 缩略图
	//    		rate=(float)2000/(float)900;
	//    		BufferedImage inputbig0 = new BufferedImage(900, (int)(height/rate),BufferedImage.TYPE_INT_RGB);
	//    	    inputbig0.getGraphics().drawImage(jgp, 0, 0, 900, (int)(height/rate), null); //画图
	//    	    ByteArrayOutputStream os0 = new ByteArrayOutputStream();  
	//    		ImageIO.write(inputbig0, "jpg", os0);
	//    		Message msg0 = new Message();
	//    		// System.out.println("----------------------uploadFileContent----------------------\n");
	//    		Map<String, Object> inParams0 = new HashMap<String, Object>();
	//    		inParams0.put("compressBucketId", bucketId);
	//    		inParams0.put("compressFilePath", "/test/" + fileName);
	//    		Map<String, CosFile> files0 = new HashMap<String, CosFile>();
	//    		files0.put("compressFile", new CosFile());
	//    		cos.uploadFileContentWithCompress(inParams0,
	//    				os0.toByteArray(), files0, msg0);
	//    		System.out.println(files0);
	//    		System.out.println(msg0);
			return "http://cos.myqcloud.com/11000436/data/test/"+fileName;
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
