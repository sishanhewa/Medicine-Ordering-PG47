package com.example.medicineordering.controller;

import com.example.medicineordering.model.Driver;
import com.example.medicineordering.service.DriverService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Map;

@Controller
@RequestMapping("/driver")
public class DriverController {
    @Autowired private DriverService driverService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(HttpSession session, Model model, RedirectAttributes ra) {
        // Check if driver is logged in
        Driver driver = (Driver) session.getAttribute("driver");
        if (driver == null) {
            ra.addFlashAttribute("error", "Access denied. Driver login required.");
            return "redirect:/login";
        }
        
        // Use the logged-in driver's ID
        int driverId = driver.getId();
        session.setAttribute("driverId", driverId);
        model.addAllAttributes(driverService.getHeader(driverId));
        model.addAttribute("assignedDeliveries", driverService.getAssigned(driverId));
        model.addAttribute("inProgressDeliveries", driverService.getInProgress(driverId));
        model.addAttribute("completedDeliveries", driverService.getCompletedToday(driverId));
        return "delivery_driver_panel";
    }

    @GetMapping("/{driverId}/dashboard")
    public String dashboard(@PathVariable int driverId, HttpSession session, Model model, RedirectAttributes ra) {
        // Check if driver is logged in
        Driver driver = (Driver) session.getAttribute("driver");
        if (driver == null) {
            ra.addFlashAttribute("error", "Access denied. Driver login required.");
            return "redirect:/login";
        }
        
        session.setAttribute("driverId", driverId);
        model.addAllAttributes(driverService.getHeader(driverId));
        model.addAttribute("assignedDeliveries", driverService.getAssigned(driverId));
        model.addAttribute("inProgressDeliveries", driverService.getInProgress(driverId));
        model.addAttribute("completedDeliveries", driverService.getCompletedToday(driverId));
        return "delivery_driver_panel";
    }

    @PostMapping(path="/toggle-availability",consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void toggle(@RequestBody Map<String,Object> body,HttpSession session){
        int driverId=(Integer)session.getAttribute("driverId");
        driverService.toggleAvailability(driverId,Boolean.TRUE.equals(body.get("available")));
    }

    @PostMapping("/start-delivery/{deliveryId}") @ResponseBody
    public void start(@PathVariable int deliveryId){driverService.startDelivery(deliveryId);}

    @PostMapping(path="/update-eta/{deliveryId}",consumes=MediaType.APPLICATION_JSON_VALUE) @ResponseBody
    public void eta(@PathVariable int deliveryId,@RequestBody Map<String,String> body){driverService.updateEta(deliveryId,body.get("eta"));}

    @PostMapping(path="/mark-delivered",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public String delivered(@RequestParam int deliveryId,@RequestParam(required=false)String recipientName,
                            @RequestParam(required=false)String notes,@RequestParam(required=false)MultipartFile photo,
                            @RequestParam(required=false,name="signatureDataUrl")String sig,HttpSession session)throws Exception{
        byte[]sigBytes=decode(sig);
        driverService.markDelivered(deliveryId,recipientName,notes,photo,sigBytes);
        int driverId=(Integer)session.getAttribute("driverId");
        return "redirect:/driver/"+driverId+"/dashboard";
    }

    @PostMapping(path="/report-issue",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public String issue(@RequestParam int deliveryId,@RequestParam String issueType,@RequestParam String description,
                        @RequestParam(required=false)MultipartFile photo,@RequestParam String action,HttpSession session)throws Exception{
        driverService.reportIssue(deliveryId,issueType,description,photo,action);
        int driverId=(Integer)session.getAttribute("driverId");
        return "redirect:/driver/"+driverId+"/dashboard";
    }

    private byte[] decode(String data){
        if(data==null||data.isBlank())return null;
        int comma=data.indexOf(',');
        return Base64.getDecoder().decode(comma>=0?data.substring(comma+1):data);
    }
}