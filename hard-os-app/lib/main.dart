import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hard_os_app/core/hardware_manager.dart';
import 'package:hard_os_app/pages/home_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider<HardwareManager>(
          create: (_) => HardwareManager(),
          dispose: (_, manager) => manager.dispose(),
        ),
      ],
      child: MaterialApp(
        title: '硬件通信应用',
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
          useMaterial3: true,
        ),
        home: const HomePage(),
      ),
    );
  }
}
