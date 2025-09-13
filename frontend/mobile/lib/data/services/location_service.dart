import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';

class LocationService extends GetxService {
  static LocationService get to => Get.find();

  /// Checks and requests location permissions, and ensures location services are enabled.
  /// Returns true if location is ready for use, false otherwise.
  Future<bool> checkAndRequestLocationReadiness() async {
    bool serviceEnabled;
    LocationPermission permission;

    // Test if location services are enabled.
    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      // Location services are not enabled, don't continue.
      print('📍 Location services are disabled.');
      return false;
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        // Permissions are denied, next time you could try
        // requesting permissions again (this is also where
        // Android's shouldShowRequestPermissionRationale
        // would come in).
        print('📍 Location permissions are denied.');
        return false;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      // Permissions are denied forever, handle appropriately.
      print(
        '📍 Location permissions are permanently denied, we cannot request permissions.',
      );
      return false;
    }

    // When we reach here, permissions are granted and a device's location services are enabled.
    print('📍 Location services and permissions are ready.');
    return true;
  }

  /// Opens the app settings for location.
  Future<void> openLocationSettings() async {
    await Geolocator.openLocationSettings();
  }

  /// Opens the app settings for permissions.
  Future<void> openAppSettings() async {
    await Geolocator.openAppSettings();
  }
}
