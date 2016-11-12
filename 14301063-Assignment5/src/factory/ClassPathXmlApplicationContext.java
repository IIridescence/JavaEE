package factory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import bean.BeanDefinition;
import bean.BeanUtil;
import bean.PropertyValue;
import bean.PropertyValues;
import test.Autowired;
import test.Component;
import resource.LocalFileResource;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

	NodeList beanList;
	
	public ClassPathXmlApplicationContext(String[] locations) {
		
		getComponent();

		LocalFileResource resource = new LocalFileResource(locations[0]);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
			beanList = document.getElementsByTagName("bean");
			for (int i = 0; i < beanList.getLength(); i++) {
				Node bean = beanList.item(i);
				String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();
				//System.out.println(beanName);
				loadBean(beanName);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//解析Bean和property并判断property中是否有ref
	private void loadBean(String argbeanName){
		if(getBean(argbeanName)==null){
			//System.out.println(argbeanName);
			for (int i = 0; i < beanList.getLength(); i++) {
				Node bean = beanList.item(i);
				BeanDefinition beandef = new BeanDefinition();
				String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
				String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();
				
				if(beanName.equals(argbeanName)){
					beandef.setBeanClassName(beanClassName);

					try {
						Class<?> beanClass = Class.forName(beanClassName);
						beandef.setBeanClass(beanClass);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					PropertyValues propertyValues = new PropertyValues();

					NodeList propertyList = bean.getChildNodes();
					for (int j = 0; j < propertyList.getLength(); j++) {
						Node property = propertyList.item(j);
						if (property instanceof Element) {
							Element ele = (Element) property;

							String name = ele.getAttribute("name");

							if (!ele.getAttribute("value").isEmpty()) {
								Class<?> type;
								try {
									type = beandef.getBeanClass().getDeclaredField(name).getType();
									Object value = ele.getAttribute("value");

									if (type == Integer.class) {
										value = Integer.parseInt((String) value);
									}

									propertyValues.AddPropertyValue(new PropertyValue(name, value));
								} catch (NoSuchFieldException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (!ele.getAttribute("ref").isEmpty()){
								String ref=ele.getAttribute("ref");
								if(getBean(ref)==null){
									loadBean(ref);
								}
							}
						}
					}					
					beandef.setPropertyValues(propertyValues);
					this.registerBeanDefinition(beanName, beandef);
				}
			}
		}		
	}
	
	//得到@component注解的类
	private void getComponent() {
		String packageName = "";
		File root = new File(System.getProperty("user.dir") + "\\src");

		try {
			loop(root, packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loop(File folder, String packageName) throws Exception {
		File[] files = folder.listFiles();

		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {

			File file = files[fileIndex];
			if (file.isDirectory()) {
				loop(file, packageName + file.getName() + ".");
			} else {
				listAllComponent(file.getName(), packageName);
			}
		}
	}

	private void listAllComponent(String filename, String packageName) {
		try {
			String name = filename.substring(0, filename.length() - 5);
			// System.out.println(packageName + name);
			Class<?> obj = Class.forName(packageName + name);

			// 找到component
			Component com = (Component) obj.getAnnotation(Component.class);
			if (com != null) {
				// System.out.println("name:" + com.value());

				BeanDefinition beandef = new BeanDefinition();
				beandef.setBeanClassName(packageName + name);
				beandef.setBeanClass(obj);
				// System.out.println(obj.getClass().getName());
				this.registerBeanDefinition(com.value(), beandef);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	protected BeanDefinition GetCreatedBean(BeanDefinition beanDefinition) {

		try {
			// set BeanClass for BeanDefinition
			
			Class<?> beanClass = beanDefinition.getBeanClass();
			//System.out.println(beanClass);
			Object bean=getAutowired(beanDefinition);
			
			if(bean==null){
				// set Bean Instance for BeanDefinition
				 bean=beanClass.newInstance();
			}
			
			List<PropertyValue> fieldDefinitionList;

			if (beanDefinition.getPropertyValues() != null) {
				fieldDefinitionList = beanDefinition.getPropertyValues().GetPropertyValues();
				for (PropertyValue propertyValue : fieldDefinitionList) {
					BeanUtil.invokeSetterMethod(bean, propertyValue.getName(), propertyValue.getValue());
				}
			}

			beanDefinition.setBean(bean);
			
			//System.out.println();

			return beanDefinition;

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//得到@autowire的类的constructor
	private Object getAutowired(BeanDefinition beanDefinition) {
	// TODO Auto-generated method stub
		Class<?> beanClass=beanDefinition.getBeanClass();
		Constructor<?>[] beanConstructor=beanClass.getConstructors();
		//System.out.println(beanConstructor.length);
		for(int i=0;i<beanConstructor.length;i++){
			Constructor<?> con=beanConstructor[i];
			Autowired auto=(Autowired) con.getAnnotation(Autowired.class);
			
			if(auto!=null){
				Class<?>[] parameterTypes=con.getParameterTypes();
				Object[] parameterObject= new Object[parameterTypes.length];
				
				for(int j=0;j<parameterTypes.length;j++){
					String[] str=parameterTypes[j].getName().split("\\.");
					int len=str.length;
					String beanId=str[len-1];
					
					if(getBean(beanId)!=null){			
						parameterObject[j]=getBean(beanId);
					}
				}
				try {
					
					return con.newInstance(parameterObject);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;		 
	}

}
