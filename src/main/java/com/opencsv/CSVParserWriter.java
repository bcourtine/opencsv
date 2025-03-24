package com.opencsv;

/*
 Copyright 2018 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.IOException;
import java.io.Writer;

/**
 * The CSVParserWriter is a replacement for the CSVWriter that allows you to pass in a ICSVParser
 * to handle the task of converting a string array to a line of CSV data.  This way you have the same class
 * creating the data as reading it.
 *
 * @author Scott Conway
 * @since 4.2
 */
public class CSVParserWriter extends AbstractCSVWriter {
    protected final ICSVParser parser;

    /**
     * Constructor for the CSVParserWriter.
     *
     * @param writer  - The writer to an underlying CSV source.
     * @param parser  - ICSVParser to convert the String array to csv formatted string.
     * @param lineEnd - Desired line end String (either "\n" or "\r\n").
     */
    public CSVParserWriter(Writer writer, ICSVParser parser, String lineEnd) {
        super(writer, lineEnd);
        this.parser = parser;
    }

    @Override
    protected void writeNext(String[] nextLine, boolean applyQuotesToAll, Appendable appendable) throws IOException {
        parser.parseToLine(nextLine, applyQuotesToAll, appendable);
        appendable.append(getLineEnd());
        getWriter().write(appendable.toString());
    }
}
