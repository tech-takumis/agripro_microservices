import 'package:another_flushbar/flushbar.dart';
import 'package:flutter/material.dart';
import 'package:mobile/data/models/api_error_response.dart';

class ErrorDisplay {
  static void showError(BuildContext context, dynamic error) {
    final ApiErrorResponse errorResponse = error is ApiErrorResponse
        ? error
        : ApiErrorResponse(
            success: false,
            message: error.toString(),
            status: 500,
            timestamp: DateTime.now(),
          );

    Flushbar(
      title: 'Error ${errorResponse.status}',
      message: errorResponse.formattedMessage,
      duration: const Duration(seconds: 3),
      margin: const EdgeInsets.all(8),
      borderRadius: BorderRadius.circular(8),
      backgroundColor: _withOpacity(errorResponse.statusColor, 0.9),
      icon: const Icon(
        Icons.error_outline,
        color: Colors.white,
      ),
      flushbarPosition: FlushbarPosition.TOP,
    ).show(context);
  }

  static void showSuccess(BuildContext context, String message) {
    Flushbar(
      message: message,
      duration: const Duration(seconds: 3),
      margin: const EdgeInsets.all(8),
      borderRadius: BorderRadius.circular(8),
      backgroundColor: _withOpacity(Colors.green, 0.9),
      icon: const Icon(
        Icons.check_circle_outline,
        color: Colors.white,
      ),
      flushbarPosition: FlushbarPosition.TOP,
    ).show(context);
  }

  static Color _withOpacity(Color color, double opacity) {
    return Color.fromRGBO(
      color.r as int,
      color.g as int,
      color.b as int,
      opacity,
    );
  }
}
