package br.com.lefranchi.hexagonal.demo.application.port.input.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateProductCommand {
    private final String name;
    private final double price;
}