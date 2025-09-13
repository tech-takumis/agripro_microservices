import '../data/models/registration_request.dart';
import '../data/models/registration_response.dart';
import '../data/services/api_service.dart';

class RegistrationManager {
  static Future<RegistrationResponse> register(String referenceNumber) async {
    final request = RegistrationRequest(referenceNumber: referenceNumber);
    final response = await ApiService.to.register(request);
    return response;
  }

  static bool isValidRsbsaId(String referenceNumber) {
    // Basic validation for RSBSA ID format
    // Adjust this based on your actual RSBSA ID format requirements
    if (referenceNumber.isEmpty) return false;

    // Remove any spaces or dashes
    final cleanId = referenceNumber.replaceAll(RegExp(r'[\s-]'), '');

    // Check if it contains only numbers and dashes (common RSBSA format)
    final rsbsaPattern = RegExp(r'^[\d-]+$');

    return rsbsaPattern.hasMatch(referenceNumber) && cleanId.length >= 6;
  }

  static String formatRsbsaId(String input) {
    // Remove any existing formatting
    String cleaned = input.replaceAll(RegExp(r'[\s-]'), '');

    // Add formatting if needed (adjust based on your RSBSA format)
    // Example: 221-02501 format
    if (cleaned.length >= 6) {
      return '${cleaned.substring(0, 3)}-${cleaned.substring(3)}';
    }

    return input;
  }
}
