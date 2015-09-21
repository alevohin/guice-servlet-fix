# guice-servlet-fix

One more way to fix terrrible "Multiple Servlet injectors detected".

To fix this problem you have to
 * Use `CustomGuiceFilter` instead of standart `GuiceFilter`
 * Request statis injection for `GuiceFilter` during configuring `Injector`
  

## Example of usage
 
 ```
 ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
         context.addEventListener(new GuiceServletContextListener() {
             @Override
             public Injector getInjector() {
                 return Guice.createInjector(
                     CustomGuiceFilter.staticInjectionModule(),
                     new AnyRealModule()
                 );
             }
         });
         context.addFilter(CustomGuiceFilter, "/*", null);
 ```
  
 Please see `CustomGuiceFilterWorks` for more examples.