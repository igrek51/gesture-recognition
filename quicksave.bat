@echo off
echo Zmiany:
git checkout
echo.

git commit -am "quick save"
echo.

echo Leci push, podaj haselko...
git push origin master

echo.
echo Koniec.
pause
