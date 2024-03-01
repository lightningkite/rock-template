//
//  ViewController.swift
//  Hammer Price
//
//  Created by Joseph Ivie on 1/3/24.
//

import UIKit
import apps

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        RootSetupIosKt.setup(self, app: { $0.app() })

        let temp = UIImageView(image: nil)
        temp.transform = CGAffineTransform.identity

    }

}

