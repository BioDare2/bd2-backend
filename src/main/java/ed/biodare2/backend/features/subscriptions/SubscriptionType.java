/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.subscriptions;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * DO NOT CHANGE THE ORDER AS IT IS USED IN DB TO MAP THE TYPES
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SubscriptionType {
    
    SYSTEM,
    FREE,
    EMBARGO_06,
    EMBARGO_10,
    EMBARGO_20,         // for the test account so it's experiment are never published
    FULL_WELCOME,
    FULL_INDIVIDUAL,
    FULL_GROUP,
    FULL_INHERITED;

    private static final long serialVersionUID = 1L;
    
}
