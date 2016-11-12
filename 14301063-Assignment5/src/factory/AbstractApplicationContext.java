package factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bean.BeanDefinition;
import test.car;

public abstract class AbstractApplicationContext implements ApplicationContext{
	private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
	
	public Object getBean(String beanName)
	{
		if(this.beanDefinitionMap.get(beanName)==null){
			return null;
		}
		else{
			return this.beanDefinitionMap.get(beanName).getBean();
		}
		
	}
	
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
	{
		beanDefinition = GetCreatedBean(beanDefinition);
		//System.out.println(((car)beanDefinition.getBean()).getCarColor());
		this.beanDefinitionMap.put(beanName, beanDefinition);
    	
        //System.out.println(beanDefinition.getBeanClassName());
	}
	
	protected abstract BeanDefinition GetCreatedBean(BeanDefinition beanDefinition);
}
