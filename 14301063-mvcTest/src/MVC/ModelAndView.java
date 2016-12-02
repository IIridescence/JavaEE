package MVC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelAndView {

	private Map<String, Object> map = new ConcurrentHashMap<String, Object>();
	private String name;
	private Olala[] olala = new Olala[1000];
	private int num = 0;
	
	public ModelAndView(){
		
	}
	
	public void setViewName(String viewname) {
		// TODO Auto-generated method stub		
		name = viewname+".jsp";
	}
	
	public String getViewName() {
		// TODO Auto-generated method stub
		return name;
	}

	public void addMap(String mapName, Object mapObj){
		map.put(mapName, mapObj);
	}
	
	public Object getMap(String mapName) {
		// TODO Auto-generated method stub
		return map.get(mapName);
	}
	
	public void addObject(String name, Object obj) {
		// TODO Auto-generated method stub
		olala[num] = new Olala();
		olala[num].name = name;
		olala[num].obj = obj;
		
		num++;
	}
	
	public Olala[] getObjcets(){
		return olala;
	}
	
	public int getNum(){
		return num;
	}

}
