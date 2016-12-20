package cn.edu.bjtu.weibo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cn.edu.bjtu.weibo.model.Picture;
import cn.edu.bjtu.weibo.service.UserAvatarsService;
import redis.clients.jedis.Jedis;

public class UserAvatarsServiceImpl implements UserAvatarsService {

	@Override
	public List<Picture> getUserAvatarList(String userId, int pageIndex, int numberPerPage) {
		List<String> values = null;
		List<Picture> pics = new ArrayList<Picture>();
		Jedis jedis = null;
		try{
			jedis = new Jedis("127.0.0.1", 6379);
			Long len = jedis.llen("PicurlOr");
			if(len < pageIndex*numberPerPage){
			}else if(len < (pageIndex+1)*numberPerPage){
				values = jedis.lrange("PicurlOr", pageIndex*numberPerPage, len-1);
			}else{
				values = jedis.lrange("PicurlOr", pageIndex*numberPerPage, (pageIndex+1)*numberPerPage-1);
			}
			for(int i=0;i<values.size();i++){
				Picture ap = new Picture();
				ap.setPicurl(values.get(i));
				pics.add(ap);
			}
		}catch (Exception e){
			return null;
		}finally{
			if(jedis.isConnected()){
				jedis.close();
			}
		}
		return pics;
	}

	@Override
		public boolean uploadUserAvatar(String userId, MultipartFile multipartFile) {
		// TODO Auto-generated method stub
		if(multipartFile!=null){
			String path = null;
			String type = null;
			String picName = multipartFile.getOriginalFilename();
			
			type = picName.indexOf(".")!=-1?picName.substring(picName.lastIndexOf(".") +1, picName.length()):null;
			
			if(type!=null){
				if("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())){
			        // 项目在容器中实际发布运行的根路径
					String realPath = null;
					// 自定义的文件名称
					String trueFileName=String.valueOf(System.currentTimeMillis())+picName;
					// 设置存放图片文件的路径
					path=realPath+/*System.getProperty("file.separator")+*/trueFileName;
					System.out.println("存放图片文件的路径:"+path);
				}else{
					System.out.println("不是正确的图片类型,请重新上传");
					return false;
					}
				}else{
					System.out.println("文件类型为空");
					return false;
				}
		}else{
			System.out.println("没有找到对应图片文件");
			return false;
		}
		
		return true;
	}

}
