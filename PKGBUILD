# Maintainer: Pin Phreek
pkgname='eventus_bottus'
pkgver=0.1.0
pkgrel=1
pkgdesc="A small Discord-Bot for scheduling meetings"
arch=('any')
url="https://github.com/PinPhreek/EventusBootus"
license=('GPL')
depends=('jre8-openjdk' 'jdk8-openjdk')
makedepends=('wget')
backup=()
source=("$pkgname::git://github.com/PinPhreek/EventusBootus")
	#"https://github.com/PinPhreek/EventusBootus/releases/download/$pkgver/eventusbottus.jar")
md5sums=('SKIP')

build() {
	cd "$pkgname"
	wget "https://github.com/PinPhreek/EventusBootus/releases/download/$pkgver/eventusbottus.jar"
}

package() {
	cd "$pkgname"
	install -Dm755 ./eventusbottus.jar "$pkgdir/usr/bin/eventusbottus.jar"
	install -Dm644 ./README.md "$pkgdir/usr/share/doc/$pkgname"
	install -Dm755 ./eventusbottus.service "$pkgdir/etc/systemd/system/eventusbottus.service"
}
