package com.freifeld.tools.quephaestus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class StdIOProvider {

    @Produces
    public PrintWriter output() {
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8)), true);
    }

    @Produces
    public InputStream input() {
        return System.in;
    }

}
