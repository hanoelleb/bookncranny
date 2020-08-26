/**
 * 
 */

function checkBillingAddress() {
	if ($("#sameAsShippingAddress").is(":checked")) {
		$(".billingAddress").prop("disabled", "true");
	} else {
		$(".billingAddress").prop("disabled", null);
	}
}

$(document).ready( function() {
	$("#sameAsShippingAddress").on("click",checkBillingAddress);
});

