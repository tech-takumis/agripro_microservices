import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class NetworkDebugWidget extends StatefulWidget {
  const NetworkDebugWidget({super.key});

  @override
  State<NetworkDebugWidget> createState() => _NetworkDebugWidgetState();
}

class _NetworkDebugWidgetState extends State<NetworkDebugWidget> {
  String _testResult = '';
  bool _isLoading = false;

  Future<void> _testConnection() async {
    setState(() {
      _isLoading = true;
      _testResult = 'Testing connection...';
    });

    try {
      // Test different URLs
      final urls = [
        'http://10.0.2.2:8000/mobile/login', // Android Emulator
        'http://127.0.0.1:8000/mobile/login', // iOS Simulator
        'http://localhost:8000/mobile/login', // Alternative
      ];

      String results = 'Connection Test Results:\n\n';

      for (String url in urls) {
        try {
          results += 'Testing: $url\n';

          final response = await http
              .post(
                Uri.parse(url),
                headers: {
                  'Content-Type': 'application/json',
                  'Accept': 'application/json',
                },
                body: jsonEncode({'username': 'test', 'password': 'test'}),
              )
              .timeout(const Duration(seconds: 5));

          results += '✅ Connected! Status: ${response.statusCode}\n';
          results +=
              'Response: ${response.body.substring(0, response.body.length > 100 ? 100 : response.body.length)}...\n\n';
        } catch (e) {
          results += '❌ Failed: ${e.toString()}\n\n';
        }
      }

      setState(() {
        _testResult = results;
      });
    } catch (e) {
      setState(() {
        _testResult = 'Test failed: ${e.toString()}';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.all(16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.network_check, color: Colors.blue),
                const SizedBox(width: 8),
                const Text(
                  'Network Debug',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const Spacer(),
                ElevatedButton(
                  onPressed: _isLoading ? null : _testConnection,
                  child:
                      _isLoading
                          ? const SizedBox(
                            width: 16,
                            height: 16,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                          : const Text('Test'),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (_testResult.isNotEmpty)
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.grey[300]!),
                ),
                child: SingleChildScrollView(
                  child: Text(
                    _testResult,
                    style: const TextStyle(
                      fontFamily: 'monospace',
                      fontSize: 12,
                    ),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
