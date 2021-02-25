package com.sammy.malum.common.book.objects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sammy.malum.common.book.BookScreen;
import net.minecraft.client.Minecraft;

import java.util.function.Predicate;

public class BookObject
{
    public BookObject returnObject;
    public boolean isHovering;
    public int hover;
    public int draw;
    public final int posX;
    public final int posY;
    public final int width;
    public final int height;
    public final Predicate<BookScreen> showPredicate;
    
    public BookObject(int posX, int posY, int width, int height, Predicate<BookScreen> showPredicate)
    {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.showPredicate = showPredicate;
    }
    
    public BookObject(int posX, int posY, int width, int height, BookObject returnObject, Predicate<BookScreen> showPredicate)
    {
        this(posX, posY, width, height, showPredicate);
        this.returnObject = returnObject;
    }
    
    public void draw(Minecraft minecraft, MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
    
    }
    
    public void interact(BookScreen screen)
    {
    
    }
}