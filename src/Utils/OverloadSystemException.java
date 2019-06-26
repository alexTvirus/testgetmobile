/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 *
 * @author Alex
 */
public class OverloadSystemException extends Exception {

    private String message;

    public OverloadSystemException(String ms) {
        this.message = ms;
    }

    public OverloadSystemException() {
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
