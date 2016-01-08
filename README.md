war-multi-security [![build](https://api.travis-ci.org/daggerok/war-multi-security.svg?branch=master)](https://travis-ci.org/daggerok/war-multi-security)
==================

**tags**

- JBoss EAP as java EE application server
- spring boot war (see daggerok.multi.web.config.Initializer) + mustache template engine + context-path resources workaround
- spring security crypt password storage and csrf (cross-site request forgery) protection
- gradle war multi project
- curl cli

### using spring boot

**checkout sources and run**

```shell
git clone https://github.com/daggerok/war-multi-security.git
$ cd $_
$ ./gradlew run -Dserver.context-path=/app
```

### using JBoss

**download, unzip configure and run JBoss EAP 6.4**

```shell
$ wget ...
$ tar ...
$ ~/dev/jboss-eap-6.4/bin/standalone.sh 
```
**locat JBoss deploy**

```shell
$ ./gradlew clean build
$ cp web/build/libs/web-1.0.0.war.original ~/dev/jboss-eap-6.4/standalone/deployments/app.war
```

NOTE: application context path will be /app

### in general...

...we provide login form, but what if some one don't like it?

what if customer wanna use his own form or auth service or whatever... but login is still required

here is a simple example with needed requests description

### login using curl with form and parsing html for getting _csrf 

*I know username, password and I wanna get http://localhost:8080/app/*

```shell
$ curl -i localhost:8080/app/
HTTP/1.1 302 Moved Temporarily
...
Location: http://localhost:8080/app/login
```

*ok, let's get login page*

```shell
$ curl -i localhost:8080/app/login
HTTP/1.1 200 OK
```

```html
<html><head><title>Login Page</title></head><body onload='document.f.username.focus();'>
<h3>Login with Username and Password</h3><form name='f' action='/login' method='POST'>
<table>
        <tr><td>User:</td><td><input type='text' name='username' value=''></td></tr>
        <tr><td>Password:</td><td><input type='password' name='password'/></td></tr>
        <tr><td colspan='2'><input name="submit" type="submit" value="Login"/></td></tr>
        <input name="_csrf" type="hidden" value="7f908242-5b7c-4d98-9f4f-2425640427b9" />
</table>
</form></body></html>
```

*understand... username, password and _csrf, let's do login!*

```shell
$ curl -i -XPOST localhost:8080/app/login -d 'username=max&password=max&_csrf=7f908242-5b7c-4d98-9f4f-2425640427b9'
HTTP/1.1 302 Moved Temporarily
...
Set-Cookie: JSESSIONID=puCKeRDJhw2f8ohyS6bJCeei; Path=/app
Location: http://localhost:8080/app/
```

*thank u.. finally I can go and visit needed page..*
    
```shell
$ curl -i localhost:8080/app/ --cookie 'JSESSIONID=puCKeRDJhw2f8ohyS6bJCeei'
HTTP/1.1 200 OK
```

and i've got my page

```html
<skip />
    <h2>
        hello, max! (:
    </h2>
    <p>do u know them?</p>
    <ul>
            <li>dag</li>
            <li>bax</li>
    </ul>
<aside>your session id: puCKeRDJhw2f8ohyS6bJCeei</aside>
<footer>2016 &copy; daggerok</footer>
</div>
</body>
</html>
```

*ok, seems like all fine, bye-bye...*

```shell
$ curl -i -XPOST localhost:8080/app/logout
HTTP/1.1 302 Moved Temporarily
...
Location: http://localhost:8080/app/login?logout
```

### login using curl with custom csrf filter without parsing html

After adding ouw custom csrf filter (see daggerok.multi.web.config.security.CsrfTokenGeneratorFilter)
and configuring ```daggerok.multi.web.config.security.WebSecurityCfg``` to use it

```java
public class WebSecurityCfg extends WebSecurityConfigurerAdapter {
    @Autowired
    private CsrfTokenGeneratorFilter csrfTokenGeneratorFilter;
    ...
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            ...
            .addFilterAfter(csrfTokenGeneratorFilter, CsrfFilter.class)
            .csrf() // csrf configuration...
```

we can get _csrf token directly from response

```shell
$ curl -i localhost:8080/login
HTTP/1.1 200 OK
...
X-Frame-Options: SAMEORIGIN
X-CSRF-HEADER: X-CSRF-TOKEN
X-CSRF-PARAM: _csrf
Set-Cookie: JSESSIONID=+YoBiZIsIzRu8hcOxHHHayHu; Path=/app
X-CSRF-TOKEN: 7b198da9-f8ff-4259-9d55-b4bfeb517cfc
```

and as we can see from html from response body - tokens are same

```html
<html><head><title>Login Page</title></head><body onload='document.f.username.focus();'>
<h3>Login with Username and Password</h3><form name='f' action='/app/login' method='POST'>
<table>
        <tr><td>User:</td><td><input type='text' name='username' value=''></td></tr>
        <tr><td>Password:</td><td><input type='password' name='password'/></td></tr>
        <tr><td colspan='2'><input name="submit" type="submit" value="Login"/></td></tr>
        <input name="_csrf" type="hidden" value="7b198da9-f8ff-4259-9d55-b4bfeb517cfc" />
</table>
</form></body></html>
```

so now for doing login we don't have to pars html at all! all needed information located in response header

let's do login, it's easy as 1, 2, 3

**1**

```shell
$ curl -i localhost:8080/app/
...
X-CSRF-HEADER: X-CSRF-TOKEN
X-CSRF-PARAM: _csrf
Set-Cookie: JSESSIONID=fwN6iInvaMAW+39SHUerMTxs; Path=/app
X-CSRF-TOKEN: 9047663f-886a-4fb2-894f-42ed52f8e9ca
Location: http://localhost:8080/app/login
```

**2**

```shell
$ curl -i -XPOST http://localhost:8080/app/login -d 'username=max&password=max&_csrf=9047663f-886a-4fb2-894f-42ed52f8e9ca'
...
Set-Cookie: JSESSIONID=RXD3Hho1sLmaUv6qEvf8PrL5; Path=/app
X-CSRF-TOKEN: 25459ff7-65a9-48e3-a30d-b1903e7d6114
Location: http://localhost:8080/app/
```

**3**

```shell
$ curl -i http://localhost:8080/app/ --cookie 'JSESSIONID=RXD3Hho1sLmaUv6qEvf8PrL5'
HTTP/1.1 200 OK
```

### do not store decoded passwords, never!

if u are good programmer, when u know - save insecure passwords it's absolutely forbidden. users privacy, MF!

our security config is not exception and of course we are supports this feature too

```java
public class WebSecurityCfg extends WebSecurityConfigurerAdapter {
    ...
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(new BCryptPasswordEncoder());
```

we also used BCryptPasswordEncoder in our PasswordGenerator encoder

so, even if u get not hashed password, you must encode it before save in database, for example as we doing in Initializer

```java
public CommandLineRunner testData(UserRepository userRepository, PasswordGenerator passwordGenerator) {
    return args -> Arrays.asList("max,dag,bax".split(",")).forEach(name ->
            userRepository.save(User.of(name, passwordGenerator.encode(name))));
```

### serving resources with changeable context-path (additional, out of the scope, off topic)

there is a workaround: always use contestPath value in template engine

to fix wrong 404 issue on static content we must provide access to the httpServlerRequest.getContextPath()

*in controller (see: ```web/src/main/java/daggerok/multi/web/ctrl/IndexController.java```)*

```java
@RequestMapping("/")
public String index(Model model, HttpServletRequest request) {
    model.addAttribute("request", request);
```

*in template (see: ```web/src/main/resources/templates/parts/header.html```)*

```html
<head>
...
{{#request}}
    {{#contextPath}}
<link rel="stylesheet" href="{{.}}/bootstrap.css">
<link rel="stylesheet" href="{{.}}/app.css">
    {{/contextPath}}
{{/request}}
```

*NOTE: in case error occurs, we must override BasicErrorController, (see: ```daggerok.multi.web.config.error.ErrorControllerImpl```)*

nice :)
