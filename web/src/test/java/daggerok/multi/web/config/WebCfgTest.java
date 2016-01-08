package daggerok.multi.web.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebCfg.class)
@WebIntegrationTest({"server.port:0", "management.port:0"})
public class WebCfgTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testApplicationContext() {
        assertNotNull("null application context", applicationContext);
    }

    @Test
    public void testApplicationBeans() {
        assertTrue("userDetailsImpl bean wasn't found", applicationContext.containsBean("userDetailsImpl"));
        assertTrue("userDetailsServiceImpl bean wasn't found", applicationContext.containsBean("userDetailsServiceImpl"));

        assertTrue("csrfTokenGeneratorFilter bean wasn't found", applicationContext.containsBean("csrfTokenGeneratorFilter"));
        assertTrue("webSecurityCfg bean wasn't found", applicationContext.containsBean("webSecurityCfg"));

        assertTrue("initializer bean wasn't found", applicationContext.containsBean("initializer"));
        assertTrue("dispatcherServlet bean wasn't found", applicationContext.containsBean("dispatcherServlet"));
        assertTrue("dispatcherServletRegistration bean wasn't found", applicationContext.containsBean("dispatcherServletRegistration"));

        assertTrue("webCfg bean wasn't found", applicationContext.containsBean("webCfg"));

        assertTrue("indexController bean wasn't found", applicationContext.containsBean("indexController"));

        assertTrue("webApplication bean wasn't found", applicationContext.containsBean("webApplication"));
    }
}