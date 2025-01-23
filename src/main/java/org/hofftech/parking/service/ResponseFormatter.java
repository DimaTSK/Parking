package org.hofftech.parking.service;

public class ResponseFormatter {

    private static final String MARKDOWN_CODE_BLOCK_START = "```\n";
    private static final String MARKDOWN_CODE_BLOCK_END = "\n```";


    public String formatAsMarkdownCodeBlock(String response) {
        return MARKDOWN_CODE_BLOCK_START + response + MARKDOWN_CODE_BLOCK_END;
    }
}