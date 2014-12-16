/*
 * Licensed to Hewlett-Packard Development Company, L.P. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

package com.hp.score.lang.cli.converters;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lesant on 12/16/2014.
 */
@Component
public class ListConverter implements Converter<List<String>> {
    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public List<String> convertFromText(String value, Class<?> targetType, String optionContext) {
        String[] values = StringUtils.commaDelimitedListToStringArray(value);
        List<String> list = new ArrayList<>();
        for (String v : values){
             list.add(v);
        }
        return list;
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext, MethodTarget target) {
        return true;
    }



}