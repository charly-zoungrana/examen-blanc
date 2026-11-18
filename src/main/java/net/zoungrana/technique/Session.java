package net.zoungrana.technique;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Session {
    private static final ThreadLocal<Set<Role>> ROLES = new ThreadLocal<>();

    private static final Map<String, Compte> COMPTES = Map.of(
            "lecteur", new Compte("demo-lecture".toCharArray(), Set.of(Role.LECTURE)),
            "editeur", new Compte("demo-edition".toCharArray(), Set.of(Role.LECTURE, Role.EDITION)));

    private Session() { }

    public static boolean connecter(String identifiant, char[] motDePasse) {
        deconnecter();
        try {
            var compte = COMPTES.get(identifiant);
            if (compte == null || !compte.correspond(motDePasse)) {
                return false;
            }
            ROLES.set(compte.roles);
            return true;
        } finally {
            Arrays.fill(motDePasse, '\0');
        }
    }

    public static void deconnecter() {
        ROLES.remove();
    }

    static void verifier(Role role) {
        var roles = ROLES.get();
        if (roles == null) {
            throw new SecurityException("Authentification requise.");
        }
        if (!roles.contains(role)) {
            throw new SecurityException("Accès refusé. Rôle requis : " + role);
        }
    }

    private static final class Compte {
        private final char[] motDePasse;
        private final Set<Role> roles;

        public Compte(char[] motDePasse, Set<Role> roles) {
            this.motDePasse = Objects.requireNonNull(motDePasse).clone();
            this.roles = Set.copyOf(roles);
        }

        private boolean correspond(char[] candidat) {
            return Arrays.equals(motDePasse, candidat);
        }
    }
}

