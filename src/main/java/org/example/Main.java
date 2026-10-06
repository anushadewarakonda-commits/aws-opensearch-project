package org.example;

import software.amazon.awssdk.services.s3.S3Client;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

                try (S3Client s3 = S3Client.builder().build()) {
                    System.out.println("Successfully connected to AWS!");
                }
            }

}