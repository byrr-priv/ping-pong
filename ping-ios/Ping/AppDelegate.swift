//
//  AppDelegate.swift
//  Ping
//
//  Created by Fredrik Bystam on 2018-02-18.
//  Copyright © 2018 Frallware. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    private var services: AppServices!

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {

        services = AppServices()
        Services.install(container: services)

        return true
    }


}

