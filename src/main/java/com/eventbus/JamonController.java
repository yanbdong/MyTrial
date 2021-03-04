/*************************************************************************
 *
 * CIeNET CONFIDENTIAL
 * __________________
 *
 *  CIeNET Technologies
 *  All Rights Reserved.
 *
 * NOTICE:  All source codes contained herein are, and remain
 * the property of CIeNET Technologies. The intellectual and technical concepts contained
 * herein are proprietary to CIeNET Technologies
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from CIeNET Technologies.
 *************************************************************************/

package com.eventbus;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


@Controller
@RequestMapping("jamon")
public class JamonController {

    @GetMapping("{name}")
    public String get(@PathVariable("name") String name) {
        return name;
    }
}
