package dev.stevenposterick.exceptions;

public class EmptyTextField extends Throwable {

    public EmptyTextField(String textField) {
        super(textField + " textfield is empty.");
    }
}
