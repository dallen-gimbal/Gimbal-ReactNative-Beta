require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-gimbal"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-gimbal
                   DESC
  s.homepage     = "https://github.com/PaeDae/react-native-gimbal-sdk"
  s.license      = "MIT"
  s.authors      = { "Gimbal" => "support@gimbal.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/PaeDae/react-native-gimbal-sdk.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "Gimbal", "~>2.82"
end
