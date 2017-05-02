package com.psychokiller.ws;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class AuthFilter implements Filter {
    static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            Enumeration<String> e = httpRequest.getHeaderNames();
            while(e.hasMoreElements()){
                String n = e.nextElement();
                logger.info("doFilter, {}: {} ",n,httpRequest.getHeader(n));
            }

            String accessToken = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if(Strings.isNullOrEmpty(accessToken)){
                throw new WebApplicationException(401);
            }
            logger.debug("CHECK: {} == {}", httpRequest.getSession().getId(), accessToken);
            if(accessToken.equals(httpRequest.getSession().getId())){
                filterChain.doFilter(request, response); //Propagate the message forward
            }
            else{
                throw new WebApplicationException(401);
            }
        }
        catch(WebApplicationException exp){
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.setStatus(exp.getResponse().getStatus());
            httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            httpResponse.getWriter().print("{'status': 'Unauthorized'}");
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    @Override
    public void destroy() {
        logger.info("destroy");
    }
}
