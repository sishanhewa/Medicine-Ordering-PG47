package com.example.medicineordering.controller;

import com.example.medicineordering.model.Medicine;
import com.example.medicineordering.model.Order;
import com.example.medicineordering.model.Cart;
import com.example.medicineordering.model.OrderItem;
import com.example.medicineordering.model.Prescription;
import com.example.medicineordering.model.Customer;
import com.example.medicineordering.model.ContactInquiry;
import com.example.medicineordering.model.User;
import com.example.medicineordering.repository.CartRepository;
import com.example.medicineordering.repository.PrescriptionRepository;
import com.example.medicineordering.repository.MedicineRepository;
import com.example.medicineordering.repository.OrderRepository;
import com.example.medicineordering.repository.OrderItemRepository;
import com.example.medicineordering.repository.CustomerRepository;
import com.example.medicineordering.repository.ContactInquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final MedicineRepository medicineRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final ContactInquiryRepository contactInquiryRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Autowired
    public CustomerController(MedicineRepository medicineRepository, OrderRepository orderRepository,
                              CartRepository cartRepository, PrescriptionRepository prescriptionRepository,
                              CustomerRepository customerRepository, OrderItemRepository orderItemRepository,
                              ContactInquiryRepository contactInquiryRepository, JdbcTemplate jdbcTemplate) {
        this.medicineRepository = medicineRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.contactInquiryRepository = contactInquiryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Shows the customer home page with a list of available medicines.
     */
    @GetMapping("/home")
    public String customerHome(Model model) {
        // Redirect legacy home to the main shop page (dashboard)
        return "redirect:/customer/dashboard";
    }

    // Redirect to main login page
    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("customerId") Integer customerId, HttpSession session, RedirectAttributes ra) {
        // Redirect to main login page
        return "redirect:/login";
    }

    // Dashboard with simple search by name/category - accessible to guests
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "category", required = false) String category,
                            Model model, HttpSession session) {
        // Allow guest access - no authentication required
        User user = (User) session.getAttribute("user");
        
        List<Medicine> medicines = medicineRepository.search(name, category);
        model.addAttribute("medicines", medicines);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("category", category == null ? "" : category);
        model.addAttribute("user", user);
        return "customer_dashboard";
    }

    // CART: CREATE (add item) - Allow guest users
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("medicineId") int medicineId,
                            @RequestParam("quantity") int quantity,
                            HttpSession session,
                            RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        
        if (quantity <= 0) {
            ra.addFlashAttribute("error", "Quantity must be greater than 0.");
            return "redirect:/customer/dashboard";
        }
        
        try {
            Medicine med = medicineRepository.findById(medicineId);
            if (med == null) {
                ra.addFlashAttribute("error", "Medicine not found.");
                return "redirect:/customer/dashboard";
            }
            
            if (med.getStockLevel() < quantity) {
                ra.addFlashAttribute("error", "Not enough stock available.");
                return "redirect:/customer/dashboard";
            }
            
            // If user is logged in, use database cart
            if (user != null && user.isCustomer()) {
                Integer customerId = user.getId();
                // Ensure Customer record exists for cart functionality
                ensureCustomerRecordExists(user);
                cartRepository.addItem(customerId, medicineId, quantity);
                ra.addFlashAttribute("success", "Item added to cart.");
            } else {
                // For guest users, use session-based cart
                addToSessionCart(session, medicineId, quantity, med);
                ra.addFlashAttribute("success", "Item added to cart. Please login to proceed to checkout.");
            }
        } catch (Exception e) {
            System.err.println("Add to cart failed. medicineId=" + medicineId + ", quantity=" + quantity);
            e.printStackTrace();
            ra.addFlashAttribute("error", "Could not add to cart. " + (e.getMessage() == null ? "" : e.getMessage()));
        }
        return "redirect:/customer/dashboard";
    }

    // CART: READ (view cart) - Allow guest users
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        List<Cart> items = new ArrayList<>();
        
        if (user != null && user.isCustomer()) {
            // Logged in user - use database cart
            Integer customerId = user.getId();
            // Ensure Customer record exists for cart functionality
            ensureCustomerRecordExists(user);
            items = cartRepository.findByCustomerId(customerId);
            if (items == null) {
                items = new ArrayList<>();
            }
        } else {
            // Guest user - use session cart
            items = getSessionCart(session);
        }
        
        // Add medicine names, stock levels, prices, and categories for display
        double totalAmount = 0.0;
        for (int i = 0; i < items.size(); i++) {
            Cart cart = items.get(i);
            try {
                Medicine med = medicineRepository.findById(cart.getMedicineId());
                if (med != null) {
                    cart.setMedicineName(med.getName());
                    cart.setStockLevel(med.getStockLevel());
                    cart.setPrice(med.getPrice());
                    cart.setCategory(med.getCategory());
                    cart.setRequiresPrescription(med.isRequiresPrescription());
                    // Calculate total for this item
                    totalAmount += med.getPrice() * cart.getQuantity();
                } else {
                    cart.setMedicineName("Medicine Not Found");
                    cart.setStockLevel(0);
                    cart.setPrice(0.0);
                    cart.setCategory("Unknown");
                    cart.setRequiresPrescription(false);
                }
            } catch (Exception e) {
                System.err.println("Error loading medicine for cart item: " + e.getMessage());
                cart.setMedicineName("Unknown Medicine");
                cart.setStockLevel(0);
                cart.setPrice(0.0);
                cart.setCategory("Unknown");
                cart.setRequiresPrescription(false);
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("totalAmount", totalAmount);
        return "customer_cart";
    }

    // CART: UPDATE quantity
    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("id") int id,
                             @RequestParam("quantity") int quantity,
                             RedirectAttributes ra) {
        if (quantity <= 0) {
            ra.addFlashAttribute("error", "Quantity must be greater than 0.");
            return "redirect:/customer/cart";
        }
        try {
            // Get cart item to check medicine ID
            Cart cartItem = cartRepository.findById(id);
            if (cartItem == null) {
                ra.addFlashAttribute("error", "Cart item not found.");
                return "redirect:/customer/cart";
            }
            
            // Check stock availability
            if (!medicineRepository.hasEnoughStock(cartItem.getMedicineId(), quantity)) {
                int availableStock = medicineRepository.getStockLevel(cartItem.getMedicineId());
                ra.addFlashAttribute("error", "Insufficient stock! Only " + availableStock + " items available.");
                return "redirect:/customer/cart";
            }
            
            cartRepository.updateQuantity(id, quantity);
            ra.addFlashAttribute("success", "Cart updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not update cart.");
        }
        return "redirect:/customer/cart";
    }

    // CART: DELETE item
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            cartRepository.removeItem(id);
            ra.addFlashAttribute("success", "Item removed.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not remove item.");
        }
        return "redirect:/customer/cart";
    }

    // PLACE ORDER from cart
    @PostMapping("/place-from-cart")
    public String placeFromCart(@RequestParam("customerName") String customerName,
                                @RequestParam("deliveryAddress") String deliveryAddress,
                                @RequestParam("paymentMethod") String paymentMethod,
                                HttpSession session,
                                RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            ra.addFlashAttribute("error", "Please login first.");
            return "redirect:/login";
        }
        Integer customerId = user.getId();
        
        // Ensure Customer record exists for cart functionality
        ensureCustomerRecordExists(user);
        if (!StringUtils.hasText(customerName) || !StringUtils.hasText(deliveryAddress)) {
            ra.addFlashAttribute("error", "Name and address are required.");
            return "redirect:/customer/cart";
        }
        
        // Validate payment method
        if (!StringUtils.hasText(paymentMethod) || 
            (!paymentMethod.equals("cash_on_delivery") && !paymentMethod.equals("card_payment"))) {
            ra.addFlashAttribute("error", "Please select a valid payment method.");
            return "redirect:/customer/cart";
        }
        
        System.out.println("Payment method selected: " + paymentMethod);
        try {
            List<Cart> items = cartRepository.findByCustomerId(customerId);
            if (items == null || items.size() == 0) {
                ra.addFlashAttribute("error", "Cart is empty.");
                return "redirect:/customer/cart";
            }
            double totalWeight = 0.0;
            for (int i = 0; i < items.size(); i++) {
                Cart c = items.get(i);
                totalWeight = totalWeight + (c.getQuantity() * 0.1);
            }
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString().substring(0, 8));
            // SECURITY FIX: Always use the logged-in user's name, not form input
            order.setCustomerName(user.getFullName());
            order.setDeliveryAddress(deliveryAddress);
            order.setStatus("Pending");
            order.setWeight(totalWeight);
            
            // SECURITY FIX: Don't store customer name in session - use user.getFullName() directly
            // session.setAttribute("customerName", user.getFullName()); // REMOVED - causes cross-contamination
            Order saved = orderRepository.save(order);
            
            // Check if order was saved successfully
            System.out.println("Order saved with ID: " + saved.getId());
            if (saved.getId() > 0) {
                // Reduce stock for each item in the cart
                boolean stockReduced = true;
                for (int i = 0; i < items.size(); i++) {
                    Cart cartItem = items.get(i);
                    boolean reduced = medicineRepository.reduceStock(cartItem.getMedicineId(), cartItem.getQuantity());
                    if (!reduced) {
                        stockReduced = false;
                        System.err.println("Failed to reduce stock for medicine ID: " + cartItem.getMedicineId());
                        break;
                    }
                }
                
                if (stockReduced) {
                    // Save order items for tracking
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (int i = 0; i < items.size(); i++) {
                        Cart cartItem = items.get(i);
                        Medicine med = medicineRepository.findById(cartItem.getMedicineId());
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(saved.getId());
                        orderItem.setMedicineId(cartItem.getMedicineId());
                        orderItem.setQuantity(cartItem.getQuantity());
                        orderItem.setPrice(med.getPrice());
                        orderItems.add(orderItem);
                    }
                    orderItemRepository.saveOrderItems(saved.getId(), orderItems);
                    
                    // Clear cart only after successful stock reduction and order item saving
                    cartRepository.clearCustomerCart(customerId);
                    ra.addFlashAttribute("success", "Order placed successfully! Order #" + saved.getOrderNumber());
                    System.out.println("Redirecting to order status page with ID: " + saved.getId());
                    return "redirect:/customer/order-status/" + saved.getId();
                } else {
                    // If stock reduction failed, delete the order and show error
                    orderRepository.deleteById(saved.getId());
                    ra.addFlashAttribute("error", "Some items are out of stock. Please check your cart and try again.");
                    return "redirect:/customer/cart";
                }
            } else {
                ra.addFlashAttribute("error", "Failed to save order. Please try again.");
                return "redirect:/customer/cart";
            }
        } catch (Exception e) {
            System.err.println("Order placement error: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Could not place order: " + e.getMessage());
            return "redirect:/customer/cart";
        }
    }

    /**
     * Handles the order placement request.
     * This now expects an Order object with customer details, and parameters for the item selected.
     */
    @PostMapping("/place")
    public String placeOrder(
            @ModelAttribute("orderForm") Order orderForm, // Collects customer name, address, etc.
            @RequestParam("medicineId") Integer medicineId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // --- Input Validation ---
        if (!StringUtils.hasText(orderForm.getCustomerName()) || !StringUtils.hasText(orderForm.getDeliveryAddress())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer Name and Delivery Address are required.");
            return "redirect:/customer/home";
        }

        if (medicineId == null || quantity == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a medicine and specify a valid quantity.");
            return "redirect:/customer/home";
        }

        // --- Core Logic: Find Medicine and Calculate Order Weight/Cost ---
        Medicine medicine;
        try {
            medicine = medicineRepository.findById(medicineId);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected medicine not found.");
            return "redirect:/customer/home";
        }

        // 1. Check stock
        if (medicine.getStockLevel() < quantity) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock available for " + medicine.getName() + ".");
            return "redirect:/customer/home";
        }

        // 2. Calculate dummy weight (e.g., 0.1kg per item)
        Double totalWeight = quantity * 0.1;

        // 3. Prepare the Order object for saving (CREATE CRUD)
        orderForm.setOrderNumber(UUID.randomUUID().toString().substring(0, 8)); // Generate a unique short order number
        orderForm.setStatus("Pending");
        orderForm.setWeight(totalWeight);
        // SECURITY FIX: Always use the logged-in user's name, not form input
        User user = (User) session.getAttribute("user");
        if (user != null) {
            orderForm.setCustomerName(user.getFullName());
        } else {
            orderForm.setCustomerName("Unknown User");
        }
        //  For now, we are NOT updating stock or creating line items.
        // This is a minimal implementation to show the flow.

        try {
            Order savedOrder = orderRepository.save(orderForm);

            // Success: Redirect to status page using the new Order ID
            return "redirect:/customer/order-status/" + savedOrder.getId();

        } catch (Exception e) {
            System.err.println("Error saving order: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Could not place order due to a system error.");
            return "redirect:/customer/home";
        }
    }

    /**
     * Displays the status of a newly placed order (READ CRUD)
     */
    @GetMapping("/order-status/{id}")
    public String viewOrderStatus(@PathVariable int id, Model model) {
        try {
            System.out.println("Looking for order with ID: " + id);
        Optional<Order> orderOptional = orderRepository.findById(id);

        if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                System.out.println("Found order: " + order.getOrderNumber() + " for customer: " + order.getCustomerName());
                
                // Get order items for detailed display
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
                System.out.println("Found " + orderItems.size() + " items for order " + id);
                
                // Add medicine details to order items
                for (OrderItem item : orderItems) {
                    try {
                        Medicine medicine = medicineRepository.findById(item.getMedicineId());
                        if (medicine != null) {
                            item.setMedicineName(medicine.getName());
                            item.setCategory(medicine.getCategory());
                            item.setRequiresPrescription(medicine.isRequiresPrescription());
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading medicine for order item: " + e.getMessage());
                        item.setMedicineName("Unknown Medicine");
                        item.setCategory("Unknown");
                        item.setRequiresPrescription(false);
                    }
                }
                
                model.addAttribute("order", order);
                model.addAttribute("orderItems", orderItems);
                return "customer_order_status_simple";
        } else {
                System.out.println("Order not found with ID: " + id);
            // Handle case where order is not found
            model.addAttribute("message", "Order not found or access denied.");
                return "error";
            }
        } catch (Exception e) {
            System.err.println("Order status error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "Error loading order status: " + e.getMessage());
            return "error";
        }
    }

    // My Orders - simple recent list
    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model) {
        System.out.println("=== MY ORDERS PAGE LOAD ===");
        
        // Check if customer is logged in
        User user = (User) session.getAttribute("user");
        System.out.println("User from session: " + (user != null ? user.getUsername() : "null"));
        System.out.println("User role: " + (user != null ? user.getRole() : "null"));
        System.out.println("Is customer: " + (user != null ? user.isCustomer() : "null"));
        
        if (user == null || !user.isCustomer()) {
            System.out.println("Redirecting to login - user is null or not a customer");
            return "redirect:/login";
        }
        
        // Get orders filtered by customer name (SECURITY: Only show this customer's orders)
        List<Order> allOrders = orderRepository.findRecent(100);
        final String customerName = user.getFullName();
        System.out.println("Filtering orders for customer: " + customerName);
        
        List<Order> orders = allOrders.stream()
            .filter(order -> {
                System.out.println("Checking order: " + order.getCustomerName() + " vs " + customerName);
                return customerName != null && order.getCustomerName() != null && 
                       order.getCustomerName().equals(customerName);
            })
            .limit(20)
            .collect(java.util.stream.Collectors.toList());
        
        // Enhance orders with additional information
        for (Order order : orders) {
            // Set order date (use current time as placeholder)
            order.setOrderDate(java.time.LocalDateTime.now());
            
            // Get item count for this order
            try {
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                order.setItemCount(orderItems.size());
            } catch (Exception e) {
                System.err.println("Error getting item count for order " + order.getId() + ": " + e.getMessage());
                order.setItemCount(0);
            }
        }
        
        System.out.println("Found " + orders.size() + " orders for customer");
        model.addAttribute("orders", orders);
        
        System.out.println("Orders loaded successfully: " + orders.size());
        return "customer_orders_simple";
    }

    // DELETE: Cancel an order
    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        try {
            System.out.println("=== CANCEL ORDER REQUEST ===");
            System.out.println("Order ID to cancel: " + id);
            
            // Check if customer is logged in
            User user = (User) session.getAttribute("user");
            if (user == null || !user.isCustomer()) {
                ra.addFlashAttribute("error", "Please login first.");
                return "redirect:/login";
            }
            
            // Check if order exists
            Optional<Order> orderOptional = orderRepository.findById(id);
            if (!orderOptional.isPresent()) {
                ra.addFlashAttribute("error", "Order not found.");
                return "redirect:/customer/orders";
            }
            
            // Get order items before deleting the order
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
            
            // Restore stock for each item in the order
            boolean stockRestored = true;
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                boolean restored = medicineRepository.restoreStock(item.getMedicineId(), item.getQuantity());
                if (!restored) {
                    stockRestored = false;
                    System.err.println("Failed to restore stock for medicine ID: " + item.getMedicineId());
                }
            }
            
            if (stockRestored) {
                // Delete order items and order
                orderItemRepository.deleteByOrderId(id);
                orderRepository.deleteById(id);
                System.out.println("Order cancelled and stock restored successfully: " + id);
                ra.addFlashAttribute("success", "Order cancelled successfully. Stock has been restored.");
            } else {
                System.err.println("Failed to restore stock for some items in order: " + id);
                ra.addFlashAttribute("error", "Order cancelled but some stock could not be restored. Please contact support.");
            }
            
            return "redirect:/customer/orders";
            
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Failed to cancel order: " + e.getMessage());
            return "redirect:/customer/orders";
        }
    }

    // PROFILE: customer profile page with notifications and stats
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        System.out.println("=== PROFILE PAGE LOAD ===");
        
        try {
            // Check if user is logged in and is a customer
            User user = (User) session.getAttribute("user");
            System.out.println("Profile - User from session: " + (user != null ? user.getUsername() : "null"));
            System.out.println("Profile - User role: " + (user != null ? user.getRole() : "null"));
            System.out.println("Profile - Is customer: " + (user != null ? user.isCustomer() : "null"));
            
            if (user == null || !user.isCustomer()) {
                System.out.println("Profile - Redirecting to login - user is null or not a customer");
                return "redirect:/login";
            }
            
            System.out.println("User ID: " + user.getId() + ", Name: " + user.getFullName());
            
            // Create a Customer object from User data for compatibility
            Customer customer = new Customer();
            customer.setId(user.getId());
            customer.setName(user.getFullName());
            customer.setEmail(user.getEmail());
            customer.setPhone(user.getPhone() != null ? user.getPhone() : "+94 77 123 4567"); // Use user's phone or default
            customer.setAddress("123 Health Street, Colombo 05, Sri Lanka"); // Default address
            
            // Get statistics - get all orders and filter by customer name
            List<Order> allOrders = orderRepository.findRecent(100);
            final String customerName = user.getFullName();
            System.out.println("Filtering orders for customer: " + customerName);
            
            List<Order> orders = allOrders.stream()
                .filter(order -> {
                    System.out.println("Checking order: " + order.getCustomerName() + " vs " + customerName);
                    return customerName != null && order.getCustomerName() != null && 
                           order.getCustomerName().equals(customerName);
                })
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
            
            System.out.println("Found " + orders.size() + " orders for customer");
            
            // If no orders found by name, show empty list (don't show other customers' orders!)
            if (orders.isEmpty()) {
                System.out.println("No orders found for this customer - showing empty list");
                orders = new ArrayList<>(); // Empty list instead of showing other customers' data
            }
            
            List<Prescription> prescriptions = prescriptionRepository.findByCustomerId(user.getId());
            List<Cart> cartItems = cartRepository.findByCustomerId(user.getId());
            
            // Get recent inquiries for this customer
            final String customerEmail = user.getEmail();
            List<ContactInquiry> inquiries = contactInquiryRepository.findAll().stream()
                .filter(inquiry -> customerEmail.equals(inquiry.getCustomerEmail()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
            
            // Count pending prescriptions (status = "Uploaded")
            long pendingPrescriptions = prescriptions.stream()
                .filter(p -> "Uploaded".equals(p.getStatus()))
                .count();
            
            model.addAttribute("user", user);
            model.addAttribute("customer", customer);
            model.addAttribute("orders", orders);
            model.addAttribute("prescriptions", prescriptions);
            model.addAttribute("inquiries", inquiries);
            model.addAttribute("ordersCount", orders.size());
            model.addAttribute("prescriptionsCount", prescriptions.size());
            model.addAttribute("pendingPrescriptions", pendingPrescriptions);
            model.addAttribute("cartItems", cartItems.size());
            
            System.out.println("Profile loaded successfully");
            return "customer_profile";
            
        } catch (Exception e) {
            System.err.println("Profile error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load profile: " + e.getMessage());
            return "error";
        }
    }

    // PRESCRIPTION: upload and list for logged-in customer
    @GetMapping("/prescriptions")
    public String prescriptionsPage(HttpSession session, Model model, RedirectAttributes ra) {
        System.out.println("=== PRESCRIPTIONS PAGE LOAD ===");
        
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            System.out.println("No user or not customer, redirecting to login");
            ra.addFlashAttribute("error", "Please login first.");
            return "redirect:/login";
        }
        Integer customerId = user.getId();
        System.out.println("Customer ID: " + customerId);
        
        System.out.println("Fetching prescriptions for customer: " + customerId);
        List<Prescription> list = prescriptionRepository.findByCustomerId(customerId);
        System.out.println("Found " + list.size() + " prescriptions");
        
        model.addAttribute("prescriptions", list);
        System.out.println("Model attributes set");
        System.out.println("=== PRESCRIPTIONS PAGE SUCCESS ===");
        
        return "customer_prescription_simple";
    }

    @PostMapping("/prescriptions/upload")
    public String uploadPrescription(@RequestParam("file") MultipartFile file,
                                     @RequestParam("deliveryAddress") String deliveryAddress,
                                     @RequestParam("deliveryWindow") String deliveryWindow,
                                     @RequestParam("contactPhone") String contactPhone,
                                     @RequestParam(value = "specialInstructions", required = false) String specialInstructions,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        System.out.println("=== UPLOAD START ===");
        
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !user.isCustomer()) {
                System.out.println("No user or not customer in session");
                ra.addFlashAttribute("error", "Please login first.");
                return "redirect:/login";
            }
            Integer customerId = user.getId();
            System.out.println("Customer ID: " + customerId);
            
            // Check if file is provided
            if (file == null || file.isEmpty()) {
                System.out.println("No file provided");
                ra.addFlashAttribute("error", "Please select a file to upload.");
                return "redirect:/customer/prescriptions";
            }
            
            // Check file name
            String original = file.getOriginalFilename();
            System.out.println("Original filename: " + original);
            
            if (original == null || original.trim().isEmpty()) {
                System.out.println("Invalid filename");
                ra.addFlashAttribute("error", "Invalid file name.");
                return "redirect:/customer/prescriptions";
            }
            
            // Check file type
            String lower = original.toLowerCase();
            System.out.println("File extension check: " + lower);
            
            if (!lower.endsWith(".png") && !lower.endsWith(".jpg") && !lower.endsWith(".jpeg") && !lower.endsWith(".pdf")) {
                System.out.println("Invalid file type");
                ra.addFlashAttribute("error", "Only PNG, JPG, JPEG, and PDF files are allowed.");
                return "redirect:/customer/prescriptions";
            }
            
            // Check file size (5MB limit)
            long fileSize = file.getSize();
            System.out.println("File size: " + fileSize + " bytes");
            
            if (fileSize > 5 * 1024 * 1024) {
                System.out.println("File too large");
                ra.addFlashAttribute("error", "File size must be less than 5MB.");
                return "redirect:/customer/prescriptions";
            }
            
            // Create upload directory in project root
            String projectRoot = System.getProperty("user.dir");
            System.out.println("Project root: " + projectRoot);
            
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(projectRoot, "uploads");
            System.out.println("Upload path: " + uploadPath.toAbsolutePath());
            
            // Create directory if it doesn't exist
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }
            
            // Generate unique filename
            String fileExtension = original.substring(original.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            java.nio.file.Path filePath = uploadPath.resolve(uniqueFileName);
            System.out.println("Target file path: " + filePath.toAbsolutePath());
            
            // Save file to disk
            file.transferTo(filePath.toFile());
            System.out.println("File saved successfully to: " + filePath.toAbsolutePath());
            
            // Ensure Customer record exists before saving prescription
            ensureCustomerRecordExists(user);
            
            // Create preliminary order with delivery details
            System.out.println("Creating preliminary order...");
            Order preliminaryOrder = new Order();
            preliminaryOrder.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            preliminaryOrder.setCustomerName(user.getFullName());
            preliminaryOrder.setDeliveryAddress(deliveryAddress);
            preliminaryOrder.setDeliveryWindow(deliveryWindow);
            preliminaryOrder.setWeight(0.5); // Default weight for prescription
            preliminaryOrder.setStatus("PENDING_PRESCRIPTION"); // Waiting for prescription approval
            preliminaryOrder.setOrderDate(java.time.LocalDateTime.now());
            preliminaryOrder.setItemCount(1);
            
            orderRepository.save(preliminaryOrder);
            System.out.println("Preliminary order created with ID: " + preliminaryOrder.getId());
            
            // Save prescription record to database
            System.out.println("Saving to database...");
            Prescription prescription = new Prescription();
            prescription.setCustomerId(customerId);
            prescription.setOrderId(preliminaryOrder.getId()); // Link to preliminary order
            prescription.setFileName(original);
            prescription.setFilePath(filePath.toString());
            prescription.setStatus("PENDING");
            
            System.out.println("Prescription object created: " + prescription.getFileName());
            System.out.println("Customer ID: " + customerId);
            System.out.println("Order ID: " + preliminaryOrder.getId());
            prescriptionRepository.save(prescription);
            System.out.println("Database save successful, ID: " + prescription.getId());
            
            ra.addFlashAttribute("success", "Prescription uploaded successfully: " + original);
            System.out.println("Flash attribute set");
            System.out.println("=== UPLOAD SUCCESS ===");
            
        } catch (Exception e) {
            System.err.println("=== UPLOAD ERROR ===");
            System.err.println("Error type: " + e.getClass().getSimpleName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        
        return "redirect:/customer/prescriptions";
    }

    // Error handler for upload issues
    @ExceptionHandler(Exception.class)
    public String handleUploadError(Exception e, RedirectAttributes ra) {
        System.err.println("Upload error: " + e.getMessage());
        e.printStackTrace();
        ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        return "redirect:/customer/prescriptions";
    }
    
    // ===== CONTACT US FUNCTIONALITY =====
    
    /**
     * Show contact us page
     */
    @GetMapping("/contact")
    public String contactUs(Model model, HttpSession session) {
        // Pre-fill form with customer data if logged in
        User user = (User) session.getAttribute("user");
        if (user != null && user.isCustomer()) {
            model.addAttribute("customerName", user.getFullName());
            model.addAttribute("customerEmail", user.getEmail());
            model.addAttribute("customerPhone", user.getPhone() != null ? user.getPhone() : "+94 77 123 4567");
        }
        return "customer_contact";
    }
    
    /**
     * Ensure Customer record exists for cart functionality
     */
    private void ensureCustomerRecordExists(User user) {
        try {
            System.out.println("Checking Customer record for User ID: " + user.getId());
            
            // First check by ID
            Optional<Customer> existingCustomerById = customerRepository.findById(user.getId());
            if (existingCustomerById.isPresent()) {
                System.out.println("Customer record already exists for User ID: " + user.getId());
                return;
            }
            
            
            // Check if Customer record exists with same email but different ID
            Optional<Customer> existingCustomerByEmail = customerRepository.findByEmail(user.getEmail());
            if (existingCustomerByEmail.isPresent()) {
                System.out.println("Customer record exists with same email but different ID. Updating ID...");
                Customer existingCustomer = existingCustomerByEmail.get();
                int oldId = existingCustomer.getId();
                customerRepository.updateId(oldId, user.getId());
                System.out.println("Updated existing Customer record ID from " + oldId + " to: " + user.getId());
                return;
            }
            
            // If no customer exists at all, create a new one
            System.out.println("No Customer record found, creating new one for User ID: " + user.getId());
            Customer customer = new Customer();
            customer.setId(user.getId());
            customer.setName(user.getFullName());
            customer.setEmail(user.getEmail());
            customer.setPhone(user.getPhone() != null ? user.getPhone() : "+94 77 123 4567");
            customer.setAddress("123 Health Street, Colombo 05, Sri Lanka");
            customer.setPassword(""); // Set empty password for Customer record
            
            customerRepository.save(customer);
            System.out.println("Created new Customer record for User ID: " + user.getId());
            
            // Verify creation
            Optional<Customer> verifyCustomer = customerRepository.findById(user.getId());
            if (verifyCustomer.isPresent()) {
                System.out.println("Verification: Customer record created successfully with ID: " + verifyCustomer.get().getId());
            } else {
                System.err.println("ERROR: Failed to create Customer record!");
                throw new RuntimeException("Failed to create Customer record for User ID: " + user.getId());
            }
        } catch (Exception e) {
            System.err.println("Error ensuring Customer record exists: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Submit contact inquiry
     */
    // Debug endpoint to check inquiries
    @GetMapping("/debug-inquiries")
    public String debugInquiries(Model model) {
        List<ContactInquiry> allInquiries = contactInquiryRepository.findAll();
        model.addAttribute("allInquiries", allInquiries);
        return "debug_inquiries";
    }
    
    // Debug endpoint to check customers and users
    @GetMapping("/debug-customers")
    public String debugCustomers(Model model) {
        List<Customer> allCustomers = customerRepository.findAll();
        model.addAttribute("customers", allCustomers);
        return "debug_customers";
    }
    
    // Debug endpoint to check specific customer by ID
    @GetMapping("/debug-customer/{id}")
    public String debugCustomer(@PathVariable int id, Model model) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            model.addAttribute("found", true);
        } else {
            model.addAttribute("found", false);
            model.addAttribute("customerId", id);
        }
        return "debug_customer";
    }
    
    // Debug endpoint to run raw SQL queries
    @GetMapping("/debug-sql")
    public String debugSql(Model model) {
        try {
            // Count total inquiries
            String countSql = "SELECT COUNT(*) as total FROM dbo.ContactInquiries";
            Integer totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
            
            // Count by status
            String statusSql = "SELECT status, COUNT(*) as count FROM dbo.ContactInquiries GROUP BY status";
            List<java.util.Map<String, Object>> statusCounts = jdbcTemplate.queryForList(statusSql);
            
            // Get recent inquiries
            String recentSql = "SELECT TOP 5 * FROM dbo.ContactInquiries ORDER BY inquiryDate DESC";
            List<java.util.Map<String, Object>> recentInquiries = jdbcTemplate.queryForList(recentSql);
            
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("statusCounts", statusCounts);
            model.addAttribute("recentInquiries", recentInquiries);
            
            return "debug_sql";
        } catch (Exception e) {
            model.addAttribute("error", "SQL Error: " + e.getMessage());
            return "debug_sql";
        }
    }

    @PostMapping("/contact")
    public String submitContact(@RequestParam("customerName") String customerName,
                               @RequestParam("customerEmail") String customerEmail,
                               @RequestParam("customerPhone") String customerPhone,
                               @RequestParam("subject") String subject,
                               @RequestParam("message") String message,
                               @RequestParam("priority") String priority,
                               RedirectAttributes ra) {
        try {
            // Validate required fields
            if (!StringUtils.hasText(customerName) || !StringUtils.hasText(customerEmail) || 
                !StringUtils.hasText(subject) || !StringUtils.hasText(message)) {
                ra.addFlashAttribute("error", "Please fill in all required fields.");
                return "redirect:/customer/contact";
            }
            
            // Create inquiry
            ContactInquiry inquiry = new ContactInquiry(customerName, customerEmail, customerPhone, 
                                                       subject, message, priority);
            
            // Save to database
            contactInquiryRepository.save(inquiry);
            
            ra.addFlashAttribute("success", "Your inquiry has been submitted successfully! We'll get back to you soon.");
            return "redirect:/customer/contact";
            
        } catch (Exception e) {
            System.err.println("Error submitting contact inquiry: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Failed to submit inquiry. Please try again.");
            return "redirect:/customer/contact";
        }
    }
    
    // Simple test endpoint to verify Customer record creation
    @GetMapping("/test-customer-creation")
    public String testCustomerCreation(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !user.isCustomer()) {
                return "redirect:/login";
            }
            
            System.out.println("=== TESTING CUSTOMER RECORD CREATION ===");
            System.out.println("User ID: " + user.getId());
            System.out.println("User Email: " + user.getEmail());
            
            // Check if Customer record exists
            Optional<Customer> existingCustomer = customerRepository.findById(user.getId());
            if (existingCustomer.isPresent()) {
                model.addAttribute("customerExists", true);
                model.addAttribute("customer", existingCustomer.get());
                System.out.println("✅ Customer record exists: " + existingCustomer.get().getName());
            } else {
                model.addAttribute("customerExists", false);
                System.out.println("❌ Customer record does not exist");
                
                // Try to create one
                System.out.println("Attempting to create Customer record...");
                ensureCustomerRecordExists(user);
                
                // Check again
                Optional<Customer> newCustomer = customerRepository.findById(user.getId());
                if (newCustomer.isPresent()) {
                    model.addAttribute("customerExists", true);
                    model.addAttribute("customer", newCustomer.get());
                    model.addAttribute("created", true);
                    System.out.println("✅ Customer record created successfully");
                } else {
                    model.addAttribute("created", false);
                    System.out.println("❌ Failed to create Customer record");
                }
            }
            
            return "test_customer_creation";
        } catch (Exception e) {
            System.err.println("Test error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Test failed: " + e.getMessage());
            return "error";
        }
    }
    
    // ===== SESSION CART HELPER METHODS =====
    
    /**
     * Add item to session-based cart for guest users
     */
    private void addToSessionCart(HttpSession session, int medicineId, int quantity, Medicine medicine) {
        @SuppressWarnings("unchecked")
        List<Cart> sessionCart = (List<Cart>) session.getAttribute("sessionCart");
        if (sessionCart == null) {
            sessionCart = new ArrayList<>();
        }
        
        // Check if item already exists in cart
        boolean itemExists = false;
        for (Cart item : sessionCart) {
            if (item.getMedicineId() == medicineId) {
                item.setQuantity(item.getQuantity() + quantity);
                itemExists = true;
                break;
            }
        }
        
        // If item doesn't exist, add new item
        if (!itemExists) {
            Cart cartItem = new Cart();
            cartItem.setMedicineId(medicineId);
            cartItem.setQuantity(quantity);
            cartItem.setMedicineName(medicine.getName());
            cartItem.setPrice(medicine.getPrice());
            cartItem.setCategory(medicine.getCategory());
            cartItem.setStockLevel(medicine.getStockLevel());
            cartItem.setRequiresPrescription(medicine.isRequiresPrescription());
            sessionCart.add(cartItem);
        }
        
        session.setAttribute("sessionCart", sessionCart);
    }
    
    /**
     * Get session-based cart for guest users
     */
    @SuppressWarnings("unchecked")
    private List<Cart> getSessionCart(HttpSession session) {
        List<Cart> sessionCart = (List<Cart>) session.getAttribute("sessionCart");
        if (sessionCart == null) {
            sessionCart = new ArrayList<>();
        }
        return sessionCart;
    }
    
    /**
     * Serve prescription files for download/viewing
     */
    @GetMapping("/prescription/file/{id}")
    public ResponseEntity<Resource> getPrescriptionFile(@PathVariable Integer id) throws IOException {
        List<Prescription> prescriptions = prescriptionRepository.findById(id);
        if (!prescriptions.isEmpty()) {
            Prescription prescription = prescriptions.get(0);
            Resource resource = new FileSystemResource(prescription.getFilePath());
            String filename = prescription.getFileName();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Re-upload prescription for rejected prescriptions
     */
    @PostMapping("/prescriptions/reupload/{id}")
    public String reuploadPrescription(@PathVariable Integer id,
                                     @RequestParam("file") MultipartFile file,
                                     HttpSession session,
                                     RedirectAttributes ra) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            ra.addFlashAttribute("error", "Please login first.");
            return "redirect:/login";
        }
        
        if (file.isEmpty()) {
            ra.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/customer/prescriptions";
        }
        
        try {
            // Get the existing prescription
            List<Prescription> prescriptions = prescriptionRepository.findById(id);
            if (prescriptions.isEmpty()) {
                ra.addFlashAttribute("error", "Prescription not found.");
                return "redirect:/customer/prescriptions";
            }
            
            Prescription prescription = prescriptions.get(0);
            
            // Check if it's actually rejected
            if (!"REJECTED".equals(prescription.getStatus())) {
                ra.addFlashAttribute("error", "Only rejected prescriptions can be re-uploaded.");
                return "redirect:/customer/prescriptions";
            }
            
            // Save the new file
            java.nio.file.Path dir = java.nio.file.Paths.get(uploadDir);
            if (!java.nio.file.Files.exists(dir)) {
                java.nio.file.Files.createDirectories(dir);
            }
            java.nio.file.Path dest = dir.resolve(System.currentTimeMillis() + "_" + file.getOriginalFilename());
            java.nio.file.Files.copy(file.getInputStream(), dest);
            
            // Update the prescription
            prescription.setFileName(file.getOriginalFilename());
            prescription.setFilePath(dest.toString());
            prescription.setStatus("PENDING");
            prescription.setRejectionReason(null); // Clear rejection reason
            prescription.setUploadDate(java.time.LocalDateTime.now());
            
            prescriptionRepository.save(prescription);
            ra.addFlashAttribute("success", "Prescription re-uploaded successfully. It will be reviewed again.");
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error re-uploading prescription: " + e.getMessage());
        }
        
        return "redirect:/customer/prescriptions";
    }
}
