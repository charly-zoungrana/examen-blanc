package net.zoungrana.technique;

import java.util.Locale;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

@Aspect
@DeclarePrecedence("net.zoungrana.technique.JournalAspect, net.zoungrana.technique.SecuriteAspect")
public class JournalAspect {
    private static final System.Logger JOURNAL = System.getLogger(JournalAspect.class.getName());

    @Around("execution(* net.zoungrana..*(..)) && !within(net.zoungrana.technique.*Aspect)")
    public Object journaliser(ProceedingJoinPoint appel) throws Throwable {
        String methode = appel.getSignature().toShortString();
        JOURNAL.log(System.Logger.Level.INFO, "Entrée  " + methode);
        long debut = System.nanoTime();
        try {
            return appel.proceed();
        } finally {
            double millisecondes = (System.nanoTime() - debut) / 1_000_000.0;
            JOURNAL.log(System.Logger.Level.INFO,
                    String.format(Locale.ROOT, "Sortie  %s (%.3f ms)", methode, millisecondes));
        }
    }
}

