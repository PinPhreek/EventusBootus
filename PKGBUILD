# Maintainer: Pin Phreek
pkgname='eventus bottus'
pkgver=1
pkgrel=1
pkgdesc="A small Discord-Bot for scheduling meeting"
arch=('all')
url="https://github.com/PinPhreek/EventusBootus"
license=('GPL')
depends=('jre8-openjdk' 'jdk8-openjdk')
makedepends=('git' 'wget')
backup=()
source=("$pkgname::git://github.com/PinPhreek/EventusBootus")
md5sums=('SKIP')

pkgver(){
	cd "$pkgname"
	printf "r%s.%s" "$(git rev-list --count HEAD)" "$(git rev-parse --short HEAD)"
}

build() {
	cd "$pkgname"
	wget https://ci.dv8tion.net/job/JDA/lastSuccessfulBuild/artifact/build/libs/JDA-4.2.0_228-withDependencies.jar
	#javac -cp *.jar -d ./build *.java
	#cd build
	jar cmvf META-INF/MANIFEST.MF eventusbottus.jar *
}

package() {
	cd "$pkgname"
	install -Dm755 ./eventusbottus.jar "$pkgdir/usr/bin/tiramisu"
	install -Dm644 ./README.md "$pkgdir/usr/share/doc/$pkgname"
}
