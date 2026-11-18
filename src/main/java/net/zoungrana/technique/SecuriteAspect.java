package net.zoungrana.technique;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class SecuriteAspect {
    @Before(value = "execution(* net.zoungrana.dessin..*(..)) && @annotation(protection)",
            argNames = "protection")
    public void controler(ExigeRole protection) {
        Session.verifier(protection.value());
    }
}
