package MVC;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DispatcherServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	ModelAndView mav;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		out.println("hello world");
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		mav = new ModelAndView();
		
		String name = request.getParameter("name");
		String pas = request.getParameter("pas");
		
		mav.addMap("name",name);
		mav.addMap("pas", pas);
		
		getController(request.getServletPath());
		
		Olala[] ola =  mav.getObjcets();
		
		for(int i=0;i<mav.getNum();i++){
			request.setAttribute(ola[i].name, ola[i].obj);
		}
		
		request.getRequestDispatcher(mav.getViewName()).forward(request, response);
	}
	
	//获取并实现@Controller
	private void getController(String url) {
		String packageName = "";
		File root = new File("D:\\software\\eclipse\\Project\\mvcTest\\src");
		
		try {
			loop(root, packageName,url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loop(File folder, String packageName, String url) throws Exception {
		File[] files = folder.listFiles();

		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {

			File file = files[fileIndex];
			if (file.isDirectory()) {
				loop(file, packageName + file.getName() + ".",url);
			} else {
				listAllController(file.getName(), packageName, url);
			}
		}
	}

	private void listAllController(String filename, String packageName,String url) {
		try {
			String name = filename.substring(0, filename.length() - 5);
			String tname = packageName + name;
			// System.out.println(packageName + name);
			Class<?> obj = Class.forName(tname);

			// 找到controller
			Controller com = (Controller) obj.getAnnotation(Controller.class);
			if (com != null) {
				// System.out.println("name:" + com.value());
				getRequestMapping(obj,url);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	//获取并实现@RequestMapping
	private void getRequestMapping(Class<?> obj,String url){
		
		Method[] objMethod = obj.getMethods();
		
		for(int i=0;i<objMethod.length;i++){
			Method method=objMethod[i];
			RequestMapping rm=(RequestMapping) method.getAnnotation(RequestMapping.class);
			
			if(rm!=null){
				RequestMapping annotation = method.getAnnotation(RequestMapping.class);
				if(url.equals(annotation.value())){
					try {
						Object[] obj1 = new  Object[1];
						obj1[0] = mav;
						mav = (ModelAndView) method.invoke(obj.newInstance(),obj1);
						
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
		    }
		}		 
	}
}
