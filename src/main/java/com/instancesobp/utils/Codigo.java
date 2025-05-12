package com.instancesobp.utils;

public enum Codigo {
    ABC(0, "ABC"),
    RND(1, "RND");

    private final int numero;
    private final String mensaje;

    Codigo(int numero, String mensaje) {
        this.numero = numero;
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public static String getTypeLocation(int numero) {
        for (Codigo codigo : Codigo.values()) {
            if (codigo.numero == numero) {
                return codigo.getMensaje();
            }
        }
        return "Número no válido";
    }
}
