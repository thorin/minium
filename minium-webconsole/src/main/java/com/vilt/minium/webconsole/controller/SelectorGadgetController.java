package com.vilt.minium.webconsole.controller;

import static com.vilt.minium.Minium.$;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vilt.minium.WebElementsDriver;
import com.vilt.minium.webconsole.SelectorGadgetWebElements;
import com.vilt.minium.webconsole.webdrivers.WebElementsDriverManager;

@Controller
@RequestMapping("/selectorGadget")
public class SelectorGadgetController {

    private static final Logger log = LoggerFactory.getLogger(SelectorGadgetController.class);
    
    @Autowired private WebElementsDriverManager wdManager;
    
    @RequestMapping(value = "/{var}/activate", method = { POST, GET })
    public ResponseEntity<Void> activateSelectorGadget(@PathVariable("var") String var, @RequestParam(value = "previousVar", required = false) String previousVar) throws Exception {
        try {
            if (StringUtils.isNotBlank(previousVar)) {
                deactivateSelectorGadget(previousVar);
            }
        } catch (Exception e) {
            log.warn("Could not deactivate previous var {}", previousVar);
        }

        SelectorGadgetWebElements elems = getSelectorGadgetWebElements(var);
        if (elems == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        elems.activateSelectorGadget();
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{var}/deactivate", method = { POST, GET })
    public ResponseEntity<Void> deactivateSelectorGadget(@PathVariable("var") String var) throws Exception {
        SelectorGadgetWebElements elems = getSelectorGadgetWebElements(var);
        if (elems == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        elems.deactivateSelectorGadget();
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{var}/cssSelector", method = { POST, GET })
    public ResponseEntity<String> getCssSelector(@PathVariable("var") String var) throws Exception {
        SelectorGadgetWebElements elems = getSelectorGadgetWebElements(var);
        if (elems == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(elems.getCssSelector(), HttpStatus.OK);
    }

    protected SelectorGadgetWebElements getSelectorGadgetWebElements(String var) {
        WebElementsDriver<?> wd = wdManager.get(var);
        if (wd == null) {
            return null;
        }
        return $(wd).cast(SelectorGadgetWebElements.class);
    }
}
