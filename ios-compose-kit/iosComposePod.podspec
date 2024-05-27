Pod::Spec.new do |spec|
    spec.name                     = 'iosComposePod'
    spec.version                  = '1.0'
    spec.homepage                 = 'https://github.com/softartdev/NoteDelight'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Common UI-kit for the NoteDelight app'
    spec.vendored_frameworks      = 'build/cocoapods/framework/iosComposeKit.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.1'
    spec.dependency 'SQLCipher', '4.5.4'
                
    if !Dir.exist?('build/cocoapods/framework/iosComposeKit.framework') || Dir.empty?('build/cocoapods/framework/iosComposeKit.framework')
        raise "

        Kotlin framework 'iosComposeKit' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :ios-compose-kit:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':ios-compose-kit',
        'PRODUCT_MODULE_NAME' => 'iosComposeKit',
    }
                
    spec.script_phases = [
        {
            :name => 'Build iosComposePod',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
    spec.resources = ['build/compose/cocoapods/compose-resources']
end