package br.com.lefranchi.hexagonal.demo.application.port.input.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProductCommand {
    private final String name;
    private final Double price;
}